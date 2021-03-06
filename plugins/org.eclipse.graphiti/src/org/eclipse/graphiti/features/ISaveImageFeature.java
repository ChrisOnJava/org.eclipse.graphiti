/*******************************************************************************
 * <copyright>
 *
 * Copyright (c) 2005, 2013 SAP AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    SAP AG - initial API, implementation and documentation
 *    mwenz - Bug 323155 - Check usage scenarios for DefaultPrintFeature and
 *            DefaultSaveImageFeature
 *    mwenz - Bug 370888 - API Access to export and print
 *
 * </copyright>
 *
 *******************************************************************************/
package org.eclipse.graphiti.features;

import org.eclipse.graphiti.features.context.ISaveImageContext;
import org.eclipse.graphiti.features.impl.AbstractSaveImageFeature;

/**
 * The Interface ISaveImageFeature for the support of the save as image
 * functionality.
 * 
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients,
 *              extend {@link AbstractSaveImageFeature} or
 *              {@link DefaultSaveImageFeature} instead.
 */
public interface ISaveImageFeature extends IFeature {

	/**
	 * Checks if save as image can be executed.
	 * 
	 * @param context
	 *            the context
	 * 
	 * @return true, if successful
	 */
	boolean canSave(ISaveImageContext context);

	/**
	 * Save the diagram using the given context information. By default the
	 * complete diagram is saved, there's currently no option to influence the
	 * saving via the context.
	 * 
	 * @param context
	 *            Context information for printing
	 * 
	 * @since 0.10
	 */
	void save(ISaveImageContext context);

	/**
	 * Pre-save hook. Called before the actual save as image process starts. You
	 * may use this hook to influence the current state of the diagram or the
	 * selection.
	 * 
	 * @param context
	 *            the context
	 */
	void preSave(ISaveImageContext context);

	/**
	 * Post-save hook. Called after the actual save as image process finished.
	 * You may use this hook to set back the changes done in the preSave method.
	 * 
	 * @param context
	 *            the context
	 */
	void postSave(ISaveImageContext context);
}
