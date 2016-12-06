package eme.handlers;

import org.eclipse.core.commands.AbstractHandler;
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
 * Main handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 * @author Timur Saglam
 */
public class MainHandler extends AbstractHandler {

    private IWorkbenchWindow window;

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
        EcoreMetamodelExtraction eme = new EcoreMetamodelExtraction();
        eme.extractFrom(chooseProject());
        return null;
    }

    /**
     * Accesses all the projects in the workspace and lets the user choose a project with a simple
     * dialog.
     * @return the chosen project.
     */
    public IProject chooseProject() {
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        IWorkspaceRoot root = workspace.getRoot();
        IProject[] projects = root.getProjects();
        for (IProject project : projects) {
            if (isChoosen(project.getName())) {
                return project;
            }
        }
        return null;
    }

    /**
     * Asks the user for one specific project with a simple message dialog.
     * @param project is the name of the project.
     * @return true if the project is chosen.
     */
    public boolean isChoosen(String project) {
        String title = "EcoreMetamodellExtraction";
        String message = "Choose " + project + "?";
        return MessageDialog.openQuestion(window.getShell(), title, message);
    }
}
