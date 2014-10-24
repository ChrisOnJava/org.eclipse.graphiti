/*******************************************************************************
 * <copyright>
 *
 * Copyright (c) 2005, 2014 SAP AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    SAP AG - initial API, implementation and documentation
 *    mwenz - Bug 324859 - Need Undo/Redo support for Non-EMF based domain objects
 *    mgorning - Bug 329517 - state call backs during creation of a connection
 *    mwenz - Bug 325084 - Provide documentation for Patterns
 *    mwenz - Bug 443304 - Improve undo/redo handling in Graphiti features
 *
 * </copyright>
 *
 *******************************************************************************/
package org.eclipse.graphiti.pattern;

import org.eclipse.graphiti.features.ICustomAbortableUndoRedoFeature;
import org.eclipse.graphiti.features.ICustomUndoableFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.ICreateConnectionContext;
import org.eclipse.graphiti.features.impl.AbstractCreateConnectionFeature;
import org.eclipse.graphiti.mm.pictograms.Connection;

/**
 * This feature wraps the create functionality of a pattern for calls of the
 * Graphiti framework. Clients should not need to use this class directly.
 * 
 * @noextend This class is not intended to be subclassed by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class CreateConnectionFeatureForPattern extends AbstractCreateConnectionFeature implements
		ICustomUndoableFeature, ICustomAbortableUndoRedoFeature {
	private IConnectionPattern delegate;

	/**
	 * Creates a new {@link CreateConnectionFeatureForPattern}.
	 * 
	 * @param featureProvider
	 *            the feature provider
	 * @param pattern
	 *            the connection pattern
	 */
	public CreateConnectionFeatureForPattern(IFeatureProvider featureProvider, IConnectionPattern pattern) {
		super(featureProvider, pattern.getCreateName(), pattern.getCreateDescription());
		delegate = pattern;
	}

	public boolean canCreate(ICreateConnectionContext context) {
		boolean ret = delegate.canCreate(context);
		return ret;
	}

	public boolean canStartConnection(ICreateConnectionContext context) {
		return delegate.canStartConnection(context);
	}

	public Connection create(ICreateConnectionContext context) {
		return delegate.create(context);
	}

	@Override
	public String getCreateImageId() {
		return delegate.getCreateImageId();
	}

	@Override
	public String getCreateLargeImageId() {
		return delegate.getCreateLargeImageId();
	}

	/**
	 * @since 0.12
	 */
	@Override
	public boolean isAbort() {
		if (delegate instanceof ICustomAbortableUndoRedoPattern) {
			return ((ICustomAbortableUndoRedoPattern) delegate).isAbort();
		}
		return false;
	}

	@Override
	public boolean canUndo(IContext context) {
		if (delegate instanceof ICustomUndoablePattern) {
			return ((ICustomUndoablePattern) delegate).canUndo(this, context);
		}
		return super.canUndo(context);
	}

	/**
	 * @since 0.12
	 */
	@Override
	public void preUndo(IContext context) {
	}

	/**
	 * @since 0.12
	 */
	@Override
	public void postUndo(IContext context) {
		if (delegate instanceof ICustomAbortableUndoRedoPattern) {
			((ICustomAbortableUndoRedoPattern) delegate).postUndo(this, context);
		}
	}

	/**
	 * @since 0.8
	 * @deprecated use {@link #postUndo(IContext)} instead
	 */
	public void undo(IContext context) {
		if (delegate instanceof ICustomUndoablePattern) {
			((ICustomUndoablePattern) delegate).undo(this, context);
		}
	}

	/**
	 * @since 0.8
	 */
	public boolean canRedo(IContext context) {
		if (delegate instanceof ICustomUndoablePattern) {
			return ((ICustomUndoablePattern) delegate).canRedo(this, context);
		}
		return true;
	}

	/**
	 * @since 0.12
	 */
	@Override
	public void preRedo(IContext context) {
	}

	/**
	 * @since 0.12
	 */
	@Override
	public void postRedo(IContext context) {
		if (delegate instanceof ICustomUndoablePattern) {
			((ICustomUndoablePattern) delegate).redo(this, context);
		}
	}

	/**
	 * @since 0.8
	 * @deprecated use {@link #postRedo(IContext)} instead
	 */
	public void redo(IContext context) {
	}

	@Override
	public void startConnecting() {
		delegate.startConnecting();
	}

	@Override
	public void endConnecting() {
		delegate.endConnecting();
	}

	@Override
	public void attachedToSource(ICreateConnectionContext context) {
		delegate.attachedToSource(context);
	}

	@Override
	public void canceledAttaching(ICreateConnectionContext context) {
		delegate.canceledAttaching(context);
	}

	/**
	 * Gets the pattern.
	 * 
	 * @return the pattern
	 * @since 0.10
	 */
	public IConnectionPattern getPattern() {
		return delegate;
	}
}
