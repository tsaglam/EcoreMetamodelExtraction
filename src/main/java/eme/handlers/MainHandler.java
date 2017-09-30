package eme.handlers;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

/**
 * Main handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 * @author Timur Saglam
 */
public abstract class MainHandler extends AbstractHandler {
    private static final Logger logger = LogManager.getLogger(MainHandler.class.getName());

    static { // Set logging level.
        Logger rootLogger = Logger.getRootLogger();
        rootLogger.setLevel(Level.INFO);
    }

    protected String title;

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
     * Tests if an {@link IProject} is a Java project.
     * @param project is the {@link IProject} to test.
     * @return true if it is a Java project, false if it isn't or an exception arises.
     */
    protected boolean isJavaProject(IProject project) {
        try {
            return project.isOpen() && project.isNatureEnabled("org.eclipse.jdt.core.javanature");
        } catch (CoreException exception) {
            logger.error(exception);
        }
        return false;
    }
}
