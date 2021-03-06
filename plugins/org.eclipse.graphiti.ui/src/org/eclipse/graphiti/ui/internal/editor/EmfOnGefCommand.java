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
 *    mwenz - Bug 324859 - Need Undo/Redo support for Non-EMF based domain objects
 *
 * </copyright>
 *
 *******************************************************************************/
package org.eclipse.graphiti.ui.internal.editor;

import java.util.Collection;

import org.eclipse.emf.common.command.Command;

/**
 * @noinstantiate This class is not intended to be instantiated by clients.
 * @noextend This class is not intended to be subclassed by clients.
 */
public class EmfOnGefCommand implements Command {

	private org.eclipse.gef.commands.Command gefCommand;

	public EmfOnGefCommand(org.eclipse.gef.commands.Command gefCommand) {
		setGefCommand(gefCommand);
	}

	public boolean canExecute() {
		return getGefCommand().canExecute();
	}

	public boolean canUndo() {
		return getGefCommand().canUndo();
	}

	public Command chain(Command command) {
		throw new IllegalArgumentException();
	}

	public void dispose() {
		getGefCommand().dispose();
	}

	public void execute() {
		org.eclipse.gef.commands.Command gefCommand = getGefCommand();
		if (gefCommand != null) {
			gefCommand.execute();
		}
	}

	public Collection<?> getAffectedObjects() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getDescription() {
		return getLabel();
	}

	public String getLabel() {
		String ret = ""; //$NON-NLS-1$
		org.eclipse.gef.commands.Command gComm = getGefCommand();
		if (gComm != null) {
			ret = gComm.getLabel();
		}
		return ret;
	}

	public Collection<?> getResult() {
		return null;
	}

	public void redo() {
		getGefCommand().redo();
	}

	public void undo() {
		getGefCommand().undo();
	}

	private void setGefCommand(org.eclipse.gef.commands.Command gefCommand) {
		this.gefCommand = gefCommand;

	}

	public org.eclipse.gef.commands.Command getGefCommand() {
		return gefCommand;
	}

}
