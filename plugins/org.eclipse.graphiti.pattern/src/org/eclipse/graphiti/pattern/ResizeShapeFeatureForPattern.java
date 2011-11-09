/*******************************************************************************
 * <copyright>
 *
 * Copyright (c) 2005, 2011 SAP AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    SAP AG - initial API, implementation and documentation
 *    Patch 185019 from Bug 332360 contributed by Volker Wegert
 *    mwenz - Bug 324859 - Need Undo/Redo support for Non-EMF based domain objects
 *
 * </copyright>
 *
 *******************************************************************************/
package org.eclipse.graphiti.pattern;

import org.eclipse.graphiti.features.ICustomUndoableFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IResizeConfiguration;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.features.impl.DefaultResizeShapeFeature;

/**
 * The Class ResizeShapeFeatureForPattern.
 */
public class ResizeShapeFeatureForPattern extends DefaultResizeShapeFeature implements ICustomUndoableFeature {

	private IFeatureForPattern delegate;

	/**
	 * Creates a new {@link ResizeShapeFeatureForPattern}.
	 * 
	 * @param featureProvider
	 *            the feature provider
	 * @param pattern
	 *            the pattern
	 */
	public ResizeShapeFeatureForPattern(IFeatureProvider featureProvider, IPattern pattern) {
		super(featureProvider);
		delegate = new FeatureForPatternDelegate(pattern);
	}

	@Override
	public boolean canResizeShape(IResizeShapeContext context) {
		IPattern pattern = delegate.getPattern();
		return pattern.canResizeShape(context);
	}

	@Override
	public void resizeShape(IResizeShapeContext context) {
		delegate.getPattern().resizeShape(context);
	}

	@Override
	public IResizeConfiguration getResizeConfiguration(IResizeShapeContext context) {
		IPattern pattern = delegate.getPattern();
		IResizeConfiguration resizeConfiguration = pattern.getResizeConfiguration(context);
		if (resizeConfiguration != null) {
			return resizeConfiguration;
		}
		return super.getResizeConfiguration(context);
	}

	@Override
	public boolean canUndo(IContext context) {
		IPattern pattern = delegate.getPattern();
		if (pattern instanceof ICustomUndoablePattern) {
			return ((ICustomUndoablePattern) pattern).canUndo(this, context);
		}
		return super.canUndo(context);
	}

	/**
	 * @since 0.8
	 */
	public void undo(IContext context) {
		IPattern pattern = delegate.getPattern();
		if (pattern instanceof ICustomUndoablePattern) {
			((ICustomUndoablePattern) pattern).undo(this, context);
		}
	}

	/**
	 * @since 0.8
	 */
	public boolean canRedo(IContext context) {
		IPattern pattern = delegate.getPattern();
		if (pattern instanceof ICustomUndoablePattern) {
			return ((ICustomUndoablePattern) pattern).canRedo(this, context);
		}
		return true;
	}

	/**
	 * @since 0.8
	 */
	public void redo(IContext context) {
		IPattern pattern = delegate.getPattern();
		if (pattern instanceof ICustomUndoablePattern) {
			((ICustomUndoablePattern) pattern).redo(this, context);
		}
	}
}
