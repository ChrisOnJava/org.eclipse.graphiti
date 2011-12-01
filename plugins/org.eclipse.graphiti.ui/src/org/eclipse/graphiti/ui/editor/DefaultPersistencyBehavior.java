package org.eclipse.graphiti.ui.editor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.internal.IDiagramVersion;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramsPackage;
import org.eclipse.graphiti.ui.internal.GraphitiUIPlugin;
import org.eclipse.graphiti.ui.internal.T;
import org.eclipse.graphiti.ui.internal.services.GraphitiUiInternal;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.PlatformUI;

/**
 * This class can be sub-classed by clients to adapt the persistency of the
 * Graphiti diagram Editor.
 * 
 * @since 0.9
 */
public class DefaultPersistencyBehavior {

	private DiagramEditor diagramEditor;

	public DefaultPersistencyBehavior(DiagramEditor diagramEditor) {
		this.diagramEditor = diagramEditor;
	}

	public Diagram loadDiagram(String uriName) {
		URI uri = URI.createURI(uriName);
		if (uri != null) {
			final TransactionalEditingDomain editingDomain = diagramEditor.getEditingDomain();
			if (editingDomain != null) {
				// First try the URI resolution without loading not yet loaded
				// resources because calling with loadOnDemand will _always_
				// create a new Resource instance for newly created and not yet
				// saved Resources, no matter if they already exist within the
				// ResourceSet or not
				EObject modelElement = editingDomain.getResourceSet().getEObject(uri, false);
				if (modelElement == null) {
					modelElement = editingDomain.getResourceSet().getEObject(uri, true);
					if (modelElement == null) {
						return null;
					}
				}
				modelElement.eResource().setTrackingModification(true);
				return (Diagram) modelElement;
			}
		}
		return null;
	}

	// TODO extract some functionality, like setting version info, saving, ...
	public void saveDiagram(IProgressMonitor monitor) {
		// set version info.
		final Diagram diagram = diagramEditor.getDiagramTypeProvider().getDiagram();
		diagramEditor.getEditingDomain().getCommandStack()
				.execute(new RecordingCommand(diagramEditor.getEditingDomain()) {

					@Override
					protected void doExecute() {
						diagram.eSet(PictogramsPackage.eINSTANCE.getDiagram_Version(), IDiagramVersion.CURRENT);
					}
				});

		// Save only resources that have actually changed.
		final Map<Object, Object> saveOptions = new HashMap<Object, Object>();
		saveOptions.put(Resource.OPTION_SAVE_ONLY_IF_CHANGED, Resource.OPTION_SAVE_ONLY_IF_CHANGED_MEMORY_BUFFER);
		final Set<Resource> savedResources = new HashSet<Resource>();

		// Do the work within an operation because this is a long running
		// activity that modifies the workbench.
		final IRunnableWithProgress operation = new IRunnableWithProgress() {
			// This is the method that gets invoked when the operation runs.
			public void run(IProgressMonitor monitor) {
				// Save the resources to the file system.
				try {
					savedResources.addAll(GraphitiUiInternal.getEmfService().save(diagramEditor.getEditingDomain()));
				} catch (final WrappedException e) {
					final MultiStatus errorStatus = new MultiStatus(GraphitiUIPlugin.PLUGIN_ID, 0, e.getMessage(),
							e.exception());
					GraphitiUIPlugin.getDefault().getLog().log(errorStatus);
					T.racer().error(e.getMessage(), e.exception());
				}
			}
		};

		diagramEditor.getBehavior().setProblemIndicationUpdateActive(false);
		try {
			// This runs the options, and shows progress.
			new ProgressMonitorDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell()).run(true, false,
					operation);

			((BasicCommandStack) diagramEditor.getEditingDomain().getCommandStack()).saveIsDone();
			// Refresh the necessary state.
			diagramEditor.updateDirtyState();
			// commandStack.notifyListeners();
		} catch (final Exception exception) {
			// Something went wrong that shouldn't.
			T.racer().error(exception.getMessage(), exception);
		}
		diagramEditor.getBehavior().setProblemIndicationUpdateActive(true);
		diagramEditor.getBehavior().updateProblemIndication();

		Resource[] savedResourcesArray = savedResources.toArray(new Resource[savedResources.size()]);
		diagramEditor.commandStackChanged(null);
		IDiagramTypeProvider provider = diagramEditor.getConfigurationProvider().getDiagramTypeProvider();
		provider.resourcesSaved(diagramEditor.getDiagramTypeProvider().getDiagram(), savedResourcesArray);
	}

}
