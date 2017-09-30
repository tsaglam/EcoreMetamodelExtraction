package eme.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import eme.EcoreMetamodelExtraction;

/**
 * Handler for calling an extraction method.
 * @author Timur Saglam
 */
public class WorkspaceHandler extends MainHandler {

    private IWorkbenchWindow window;

    /**
     * Accesses all the projects in the workspace and lets the user choose a project with a simple dialog.
     * @return the chosen project.
     */
    public IProject chooseProject() {
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        IWorkspaceRoot root = workspace.getRoot();
        IProject[] projects = root.getProjects();
        for (IProject project : projects) {
            if (isJavaProject(project) && isChoosen(project)) {
                return project;
            }
        }
        return null;
    }

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
        IProject project = chooseProject();
        if (project != null) {
            new EcoreMetamodelExtraction().extract(project);
        }
        return null;
    }

    /**
     * Asks the user for one specific project with a simple message dialog.
     * @param project is the name of the project.
     * @return true if the project is chosen.
     */
    private boolean isChoosen(IProject project) {
        String message = "Choose " + project.getName() + "?";
        return MessageDialog.openQuestion(window.getShell(), title, message);
    }
}