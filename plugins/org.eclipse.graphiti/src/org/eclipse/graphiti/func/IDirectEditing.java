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
 *    Volker Wegert - Bug 332363 - Direct Editing: enable automatic resizing for combo boxes
 *    mgorning - Bug 347262 - DirectEditingFeature with TYPE_DIALOG type
 *
 * </copyright>
 *
 *******************************************************************************/
package org.eclipse.graphiti.func;

import org.eclipse.graphiti.features.IDirectEditingFeature;
import org.eclipse.graphiti.features.context.IDirectEditingContext;

/**
 * The Interface IDirectEditing.
 * 
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface IDirectEditing {

	/**
	 * no special UI is wanted for editing.
	 */
	static final int TYPE_NONE = 0;

	/**
	 * text field is wanted for editing.
	 */
	static final int TYPE_TEXT = 1;

	/**
	 * multi line text box is wanted for editing.
	 */
	static final int TYPE_MULTILINETEXT = 5;

	/**
	 * combo box is wanted for editing.
	 */
	static final int TYPE_DROPDOWN = 3;

	/**
	 * read only combo box is wanted for editing.
	 */
	static final int TYPE_DROPDOWN_READ_ONLY = 6;

	/**
	 * With this type the customers can provide their own cell editors for the
	 * direct editing mode. In this case the direct editing feature must
	 * implement the interface
	 * <code>org.eclipse.graphiti.ui.platform.ICellEditorProvider</code>
	 * 
	 * @since 0.9
	 */
	static final int TYPE_CUSTOM = 9;

	/**
	 * Framework calls this method to decide which UI to show up.
	 * 
	 * @return the editing type
	 */
	int getEditingType();

	/**
	 * Checks if the feature can perform direct editing on the context handed
	 * over.
	 * 
	 * @param context
	 *            the context
	 * @return This method is equivalent to
	 * @see IDirectEditingFeature#canExecute(org.eclipse.graphiti.features.context.IContext)
	 */
	public boolean canDirectEdit(IDirectEditingContext context);

	/**
	 * Provides the initial value for the opened text editing UI component.
	 * 
	 * @param context
	 *            the context
	 * @return the initial value
	 */
	String getInitialValue(IDirectEditingContext context);

	/**
	 * This value will be used if the cell editor is a combo box. This
	 * functionality only applies to TYPE_DROPDOWN.
	 * 
	 * @param context
	 *            the context
	 * @return the possible values for the combo box.
	 */
	String[] getPossibleValues(IDirectEditingContext context);

	/**
	 * This proposals will be used for the completion list of a simple text cell
	 * editor. This functionality only applies to TYPE_TEXT.
	 * 
	 * @param value
	 *            current value
	 * @param caretPosition
	 *            current cursor position
	 * @param context
	 *            the context
	 * @return the proposed values
	 */
	String[] getValueProposals(String value, int caretPosition, IDirectEditingContext context);

	/**
	 * Framework calls this method to let the feature calculate the new value.
	 * 
	 * @param value
	 *            current value
	 * @param caretPosition
	 *            current cursor position
	 * @param choosenValue
	 *            value choosen by user
	 * @param context
	 *            the context
	 * @return the new value
	 */
	String completeValue(String value, int caretPosition, String choosenValue, IDirectEditingContext context);

	/**
	 * Checks if completion is available. This functionality only applies to
	 * TYPE_TEXT.
	 * 
	 * @return true if completion is / proposals are available at all
	 */
	boolean isCompletionAvailable();

	/**
	 * Checks if auto completion is enabled. This functionality only applies to
	 * TYPE_TEXT.
	 * 
	 * @return true, if proposals should appear automatically
	 */
	boolean isAutoCompletionEnabled();

	/**
	 * Stretch input field to fit its contents. This functionality applies to
	 * TYPE_TEXT, TYPE_DROPDOWN and TYPE_DROPDOWN_READ_ONLY.
	 * 
	 * @return true if the field should exactly fit the contents
	 */
	boolean stretchFieldToFitText();

	/**
	 * This method will be called by clients many times to see if current value
	 * is valid and could be set.
	 * 
	 * @param value
	 *            the value
	 * @param context
	 *            the context
	 * @return null if value is okay and could be set; any text means value is
	 *         not valid; text is reason for invalidality
	 */
	String checkValueValid(String value, IDirectEditingContext context);

	/**
	 * Set the new value. The value comes from the text editing UI component.
	 * 
	 * @param value
	 *            the value
	 * @param context
	 *            the context
	 */
	void setValue(String value, IDirectEditingContext context);

	/**
	 * The direct editing mode contains controls for code completion and the
	 * selection from a combo box. In both cases the standard implementation
	 * supports only strings.
	 * <p>
	 * If the customer wants to work with Objects he must provide an
	 * implementation of {@link IProposalSupport}. In this case the following
	 * methods of {@link IDirectEditing} are ignored:
	 * <p>
	 * <code>
	 * <br>* String checkValueValid(String value, IDirectEditingContext context);
	 * <br>* String completeValue(String value, int caretPosition, String choosenValue, IDirectEditingContext context);
	 * <br>* String[] getPossibleValues(IDirectEditingContext context);
	 * <br>* String[] getValueProposals(String value, int caretPosition, IDirectEditingContext context);
	 * <br>* void setValue(String value, IDirectEditingContext context);  
	 * </code>
	 * 
	 * @return the special implementation to support Objects in code completion
	 *         and combo box
	 * @since 0.8
	 */
	IProposalSupport getProposalSupport();
}
