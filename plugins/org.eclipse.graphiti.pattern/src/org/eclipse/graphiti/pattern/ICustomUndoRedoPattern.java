/*******************************************************************************
 * <copyright>
 *
 * Copyright (c) 2014, 2014 SAP AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    mwenz - Bug 443304 - Improve undo/redo handling in Graphiti features
 *
 * </copyright>
 *
 *******************************************************************************/
package org.eclipse.graphiti.pattern;

import org.eclipse.graphiti.features.IFeature;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

/**
 * This interface can by used and implemented by customers within any pattern to
 * signal the need for additional work that needs to be done before or after
 * undo and redo. When a pattern implements this interface, and the framework
 * performs an undo or a redo, the framework will call the contained methods.
 * <p>
 * Implementing this interface is especially helpful if customers want to
 * implement undo/redo functionality for non-EMF changes, e.g. for non-EMF
 * domain models. Note that any EMF-model change (including the changes done to
 * the graphical representation (Graphiti {@link PictogramElement}s and
 * {@link GraphicsAlgorithm}s will by handled automatically by the Graphiti
 * framework no matter if this interface is implemented by a pattern or not. The
 * pattern may use the context and feature objects (e.g. the contained
 * properties set) passed to the contained methods while executing the pattern
 * in order to collect any information needed for undo.
 * <p>
 * In case you want to cancel undo/redo operations in {@link #preUndo(IContext)}/{@link #preRedo(IContext)}, you need to implement
 * {@link ICustomAbortableUndoRedoPattern} which offers an
 * {@link ICustomAbortableUndoRedoPattern#isAbort()} method that causes the
 * cancellation of undo/redo operation in case <code>true</code> is returned.
 * 
 * @see ICustomAbortableUndoRedoPattern
 * @since 0.12
 */
public interface ICustomUndoRedoPattern {

	/**
	 * Decides if the changes done by a processed pattern functionality can be
	 * undone. This method is called once by the Graphiti framework just before
	 * any undo work is started, e.g. before {@link #preUndo(IContext)}.
	 * <p>
	 * Note that as soon as any pattern reports <code>false</code> here, also
	 * all previous entries in the command stack are no longer reachable for
	 * undo.
	 * 
	 * @param feature
	 *            this is the instance of the {@link IFeature} object that was
	 *            in use when executing the pattern functionality
	 * @param context
	 *            this is the instance of the {@link IContext} object that was
	 *            in use when executing the feature.
	 * 
	 * @return true if the feature can be undone, false if not
	 */
	boolean canUndo(IFeature feature, IContext context);

	/**
	 * This method will be called by the Graphiti framework before the EMF undo
	 * is triggered. Customers may revert their non-EMF changes done by the
	 * pattern functionality here or in {@link #postUndo(IContext)}.
	 * 
	 * @param feature
	 *            this is the instance of the {@link IFeature} object that was
	 *            in use when executing the pattern functionality
	 * @param context
	 *            this is the instance of the {@link IContext} object that was
	 *            in use when executing the feature
	 */
	void preUndo(IFeature feature, IContext context);

	/**
	 * This method will be called by the Graphiti framework after the EMF undo
	 * is finished. Customers may revert their non-EMF changes done by the
	 * pattern functionality here or in {@link #preUndo(IContext)}.
	 * 
	 * @param feature
	 *            this is the instance of the {@link IFeature} object that was
	 *            in use when executing the pattern functionality
	 * @param context
	 *            this is the instance of the {@link IContext} object that was
	 *            in use when executing the feature
	 */
	void postUndo(IFeature feature, IContext context);

	/**
	 * Decides if the processed feature can be re-done. This method is called
	 * once by the Graphiti framework just before any redo work is started, e.g.
	 * before {@link #preRedo(IContext)}.
	 * <p>
	 * Note that as soon as any pattern reports <code>false</code> here, also
	 * all consecutive entries in the command stack are no longer reachable for
	 * redo.
	 * 
	 * @param feature
	 *            this is the instance of the {@link IFeature} object that was
	 *            in use when executing the pattern functionality
	 * @param context
	 *            this is the instance of the {@link IContext} object that was
	 *            in use when executing the feature
	 * 
	 * @return true if the feature can be re-done, false if not
	 */
	boolean canRedo(IFeature feature, IContext context);

	/**
	 * This method will be called by the Graphiti framework before the EMF undo
	 * has triggered. Customers may re-apply their non-EMF changes done by the
	 * pattern functionality here or in {@link #postRedo(IContext)}. (Usually it
	 * might be sufficient to delegate to the execution method of the feature.)
	 * 
	 * @param feature
	 *            this is the instance of the {@link IFeature} object that was
	 *            in use when executing the pattern functionality
	 * @param context
	 *            this is the instance of the {@link IContext} object that was
	 *            in use when executing the feature
	 */
	void preRedo(IFeature feature, IContext context);

	/**
	 * This method will be called by the Graphiti framework after the EMF undo
	 * has finished. Customers may re-apply their non-EMF changes done by the
	 * pattern functionality here or in {@link #preRedo(IContext)}. (Usually it
	 * might be sufficient to delegate to the execution method of the feature.)
	 * 
	 * @param feature
	 *            this is the instance of the {@link IFeature} object that was
	 *            in use when executing the pattern functionality
	 * @param context
	 *            this is the instance of the {@link IContext} object that was
	 *            in use when executing the feature
	 */
	void postRedo(IFeature feature, IContext context);
}
