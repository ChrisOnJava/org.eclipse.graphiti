/*******************************************************************************
 * <copyright>
 *
 * Copyright (c) 2005, 2012 SAP AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    fvelasco - Bug 323349 External feature invocation
 *
 * </copyright>
 *
 *******************************************************************************/
package org.eclipse.graphiti.testtool.sketch;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public class ShrinkFeature extends AbstractCustomFeature {

	public static final String HINT = "shrink"; //$NON-NLS-1$

	public ShrinkFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public String getName() {
		return "Shrink"; //$NON-NLS-1$
	}

	@Override
	public boolean canExecute(ICustomContext context) {
		return context.getPictogramElements().length > 0;
	}

	public void execute(ICustomContext context) {
		PictogramElement[] pes = context.getPictogramElements();
		for (PictogramElement pe : pes) {
			GraphicsAlgorithm ga = pe.getGraphicsAlgorithm();
			if (ga == null)
				continue;
			int width = ga.getWidth();
			int height = ga.getHeight();
			ga.setWidth((int) (width * 0.9));
			ga.setHeight((int) (height * 0.9));
			layoutPictogramElement(pe);
		}
	}

}
