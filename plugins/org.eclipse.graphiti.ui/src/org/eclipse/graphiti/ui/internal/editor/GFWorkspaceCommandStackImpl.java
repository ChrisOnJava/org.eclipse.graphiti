/*******************************************************************************
 * <copyright>
 *
 * Copyright (c) 2005, 2015 SAP AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    SAP AG - initial API, implementation and documentation
 *    mwenz - Bug 324859 - Need Undo/Redo support for Non-EMF based domain objects
 *    mwenz - Bug 340627 - Features should be able to indicate cancellation
 *    mwenz - Bug 351053 - Remove the need for WorkspaceCommandStackImpl
 *    mwenz - Bug 371717 - IllegalStateException When updating cells on Diagram
 *    mwenz - Bug 389380 - Undo/Redo handling wrong Command executed by undo action
 *    mwenz - Bug 430609 - Re-entrance in diagram update causes transaction error
 *    mwenz - Bug 443304 - Improve undo/redo handling in Graphiti features
 *    mwenz - Bug 464596 - BasicIndexOutOfBoundsException in BasicEList.get
 *    Laurent Le Moux - mwenz - Bug 475240 - Malfunctioning redo GFWorkspaceCommandStackImpl
 *    mwenz - Bug 477083 - Read-write transaction not created if another thread is using runExclusive()
 *    mwenz - Bug 481994 - canUndo/canRedo called twice
 *
 * </copyright>
 *
 *******************************************************************************/
package org.eclipse.graphiti.ui.internal.editor;

import java.util.Map;
import java.util.Stack;

import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.transaction.RollbackException;
import org.eclipse.emf.transaction.impl.InternalTransaction;
import org.eclipse.emf.workspace.impl.WorkspaceCommandStackImpl;
import org.eclipse.graphiti.IExecutionInfo;
import org.eclipse.graphiti.features.ICustomAbortableUndoRedoFeature;
import org.eclipse.graphiti.features.ICustomUndoRedoFeature;
import org.eclipse.graphiti.features.ICustomUndoableFeature;
import org.eclipse.graphiti.features.IFeature;
import org.eclipse.graphiti.features.IFeatureAndContext;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.internal.command.DefaultExecutionInfo;
import org.eclipse.graphiti.ui.internal.services.GraphitiUiInternal;

/**
 * @noinstantiate This class is not intended to be instantiated by clients.
 * @noextend This class is not intended to be subclassed by clients.
 */
public class GFWorkspaceCommandStackImpl extends WorkspaceCommandStackImpl {

	private Stack<IExecutionInfo> undoStackForExecutionInfo = new Stack<IExecutionInfo>();

	private Stack<IExecutionInfo> redoStackForExecutionInfo = new Stack<IExecutionInfo>();

	public GFWorkspaceCommandStackImpl(IOperationHistory history) {
		super(history);
	}

	/*
	 * Bug 371717: Added synchronization to this method to support scenarios
	 * where the diagram is changed with the help of an externally triggered
	 * thread (e.g. operation triggered by a context menu from the project
	 * explorer). In general: avoid different threads outrunning each other on
	 * topLevelCommand.
	 * 
	 * @see
	 * org.eclipse.emf.transaction.impl.AbstractTransactionalCommandStack#execute
	 * (org.eclipse.emf.common.command.Command, java.util.Map)
	 */
	@Override
	public synchronized void execute(Command command, Map<?, ?> options) throws InterruptedException, RollbackException {
		IExecutionInfo executionInfo = null;
		/*
		 * Needed to retrieve an eventually externally created IExecutionInfo
		 * object (created in GFCommandStack) to the internal execution of a
		 * feature. Could be created here as well, but then there would
		 * potentially be different instances of features and contexts.
		 */
		if (options != null) {
			Object object = options.get(GFCommandStack.OPTION_EXECUTION_INFO);
			if (object instanceof IExecutionInfo) {
				executionInfo = (IExecutionInfo) object;
				options.remove(GFCommandStack.OPTION_EXECUTION_INFO);
			}
		}

		InternalTransaction activeTransaction = getDomain().getActiveTransaction();
		boolean usableTransactionExists = activeTransaction != null && !activeTransaction.isReadOnly()
				&& activeTransaction.getOwner().equals(Thread.currentThread());

		if (!usableTransactionExists) {
			// No active transaction means we have to execute the command as a
			// top-level command
			super.execute(command, options);

			/*
			 * Add the execution info to the stack; in case it is not provided,
			 * create it first. Must happen here and not in the else branch
			 * below because the super.execute call add the EMF command to the
			 * history and therefore to the command stack (thus is the entry
			 * relevant for undo)
			 */
			if (executionInfo == null) {
				executionInfo = new DefaultExecutionInfo();
				GraphitiUiInternal.getCommandService().completeExecutionInfo((DefaultExecutionInfo) executionInfo,
						GraphitiUiInternal.getCommandService().transformFromEmfToGefCommand(command));
			}

			/*
			 * Remove the feature and context combinations from the execution
			 * list whose features did not do any changes. The commands for
			 * those features are not placed on the editor command stack and
			 * must also not appear in the stack for additional undo steps. See
			 * Bugzilla 389380 for details. In case no entry is left in the
			 * execution info, it must not be written to the stack in order to
			 * keep the standard and the additional undo stack in sync.
			 */
			executionInfo = GraphitiUiInternal.getCommandService().removeFeaturesWithoutChanges(executionInfo);
			if (executionInfo.getExecutionList().length > 0) {
				undoStackForExecutionInfo.push(executionInfo);
			}
		} else {
			// An active transaction already exists, execute the command within
			// the its scope
			command.execute();
			if (getMostRecentCommand() != null) {
				getMostRecentCommand().chain(command);
			}
		}
	}

	@Override
	public void dispose() {
		super.dispose();
		redoStackForExecutionInfo.clear();
		redoStackForExecutionInfo = null;
		undoStackForExecutionInfo.clear();
		undoStackForExecutionInfo = null;
	}

	@Override
	public void flush() {
		super.flush();
		undoStackForExecutionInfo.clear();
		redoStackForExecutionInfo.clear();
	}

	@Override
	public void redo() {
		IFeatureAndContext[] executionList = null;
		boolean[] canRedo = null;
		if (!redoStackForExecutionInfo.isEmpty()) {
			IExecutionInfo ei = redoStackForExecutionInfo.pop();
			undoStackForExecutionInfo.push(ei);
			executionList = ei.getExecutionList();
			canRedo = new boolean[executionList.length];
			for (int i = 0; i < executionList.length; i++) {
				IFeature feature = executionList[i].getFeature();
				IContext context = executionList[i].getContext();
				if (feature instanceof ICustomUndoableFeature) {
					canRedo[i] = ((ICustomUndoableFeature) feature).canRedo(context);
				} else if (feature instanceof ICustomUndoRedoFeature) {
					canRedo[i] = ((ICustomUndoRedoFeature) feature).canRedo(context);
				} else {
					canRedo[i] = true;
				}
			}
		}

		// Care about non-EMF pre-redo
		if (executionList != null) {
			// Traverse operation forwards
			for (int i = executionList.length - 1; i >= 0; i--) {
				IFeature feature = executionList[i].getFeature();
				IContext context = executionList[i].getContext();
				if (feature instanceof ICustomUndoRedoFeature) {
					ICustomUndoRedoFeature undoableFeature = (ICustomUndoRedoFeature) feature;
					if (canRedo[i]) {
						undoableFeature.preRedo(context);
						if (undoableFeature instanceof ICustomAbortableUndoRedoFeature) {
							if (((ICustomAbortableUndoRedoFeature) undoableFeature).isAbort()) {
								throw new OperationCanceledException();
							}
						}
					}
				}
			}
		}

		// Trigger EMF redo
		if (super.canRedo()) {
			super.redo();
		}

		// Check if non-EMF redo is needed and care about it
		if (executionList != null) {
			// Traverse operation forwards
			for (int i = 0; i < executionList.length; i++) {
				IFeature feature = executionList[i].getFeature();
				IContext context = executionList[i].getContext();
				if (feature instanceof ICustomUndoableFeature) {
					ICustomUndoableFeature undoableFeature = (ICustomUndoableFeature) feature;
					if (canRedo[i]) {
						undoableFeature.redo(context);
					}
				}
			}
			for (int i = 0; i < executionList.length; i++) {
				IFeature feature = executionList[i].getFeature();
				IContext context = executionList[i].getContext();
				if (feature instanceof ICustomUndoRedoFeature) {
					ICustomUndoRedoFeature undoableFeature = (ICustomUndoRedoFeature) feature;
					if (canRedo[i]) {
						undoableFeature.postRedo(context);
					}
				}
			}
		}
	}

	@Override
	public void undo() {
		IFeatureAndContext[] executionList = null;
		boolean[] canUndo = null;
		if (!undoStackForExecutionInfo.isEmpty()) {
			IExecutionInfo ei = undoStackForExecutionInfo.pop();
			redoStackForExecutionInfo.push(ei);
			executionList = ei.getExecutionList();
			canUndo = new boolean[executionList.length];
			for (int i = 0; i < executionList.length; i++) {
				IFeature feature = executionList[i].getFeature();
				IContext context = executionList[i].getContext();
				canUndo[i] = feature.canUndo(context);
			}
		}

		// Care about non-EMF pre-undo
		if (executionList != null) {
			// Traverse operations backwards
			for (int i = executionList.length - 1; i >= 0; i--) {
				IFeature feature = executionList[i].getFeature();
				IContext context = executionList[i].getContext();
				if (feature instanceof ICustomUndoRedoFeature) {
					ICustomUndoRedoFeature undoableFeature = (ICustomUndoRedoFeature) feature;
					if (canUndo[i]) {
						undoableFeature.preUndo(context);
						if (undoableFeature instanceof ICustomAbortableUndoRedoFeature) {
							if (((ICustomAbortableUndoRedoFeature) undoableFeature).isAbort()) {
								throw new OperationCanceledException();
							}
						}
					}
				}
			}
		}

		// Trigger EMF undo
		if (super.canUndo()) {
			super.undo();
		}

		// Care about non-EMF undo and post-undo
		if (executionList != null) {
			// Traverse operations backwards (individually for undo and
			// post-undo)
			for (int i = executionList.length - 1; i >= 0; i--) {
				IFeature feature = executionList[i].getFeature();
				IContext context = executionList[i].getContext();
				if (feature instanceof ICustomUndoableFeature) {
					ICustomUndoableFeature undoableFeature = (ICustomUndoableFeature) feature;
					if (canUndo[i]) {
						undoableFeature.undo(context);
					}
				}
			}
			for (int i = executionList.length - 1; i >= 0; i--) {
				IFeature feature = executionList[i].getFeature();
				IContext context = executionList[i].getContext();
				if (feature instanceof ICustomUndoRedoFeature) {
					ICustomUndoRedoFeature undoableFeature = (ICustomUndoRedoFeature) feature;
					if (canUndo[i]) {
						undoableFeature.postUndo(context);
					}
				}
			}
		}
	}
}
