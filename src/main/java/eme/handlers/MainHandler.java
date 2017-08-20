package eme.handlers;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
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
public abstract class MainHandler extends AbstractHandler {
    private static final Logger logger = LogManager.getLogger(MainHandler.class.getName());
    private IWorkbenchWindow window;
    protected String title;

    static { // Set logging level.
        Logger rootLogger = Logger.getRootLogger();
        rootLogger.setLevel(Level.INFO);
    }

    /**
     * Basic constructor with default title.
     */
    public MainHandler() {
        title = "EcoreMetamodelExtraction";
    }

    /**
     * Constructor for setting a custom title;
     * @param title is the custom title.
     */
    public MainHandler(String title) {
        this.title = title;
    }

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
            startExtraction(project);
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

    /**
     * Tests if the project is a Java project.
     * @param project is the project to test.
     * @return true if it is a Java project, false if it isn't or an exception arises.
     */
    private boolean isJavaProject(IProject project) {
        try {
            return project.isOpen() && project.isNatureEnabled("org.eclipse.jdt.core.javanature");
        } catch (CoreException exception) {
            logger.error(exception);
        }
        return false;
    }

    /**
     * Starts the extraction by calling an extraction method from the class {@link EcoreMetamodelExtraction}.
     * @param project is the parameter for the methods that is called.
     */
    protected abstract void startExtraction(IProject project);
}
