package eme.generator.saving;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;

/**
 * Saving Strategy that saves in an existing Eclipse Project.
 * @author Timur Saglam
 */
public class OutputProjectSavingStrategy extends AbstractSavingStrategy {
    private final DateTimeFormatter formatter;
    private final String outputProjectName;
    private String projectName;
    private final IWorkspace workspace;

    /**
     * Basic constructor.
     * @param outputProjectName is the name of the Eclipse project where models will be saved in.
     */
    public OutputProjectSavingStrategy(String outputProjectName) {
        super(true);
        this.outputProjectName = outputProjectName;
        workspace = ResourcesPlugin.getWorkspace();
        formatter = DateTimeFormatter.ofPattern("-yyyy-MM-dd-HH:mm.ss"); // date time format
        outputProjectCheck();
    }

    /**
     * Check whether the output project exists.
     */
    private void outputProjectCheck() {
        IWorkspaceRoot root = workspace.getRoot();
        IProject[] projects = root.getProjects();
        for (IProject iProject : projects) {
            if (iProject.getName().equals(outputProjectName)) {
                return;
            }
        }
        throw new IllegalArgumentException("The specified output project could not be found: " + outputProjectName);
    }

    /*
     * @see eme.generator.saving.AbstractSavingStrategy#beforeSaving(java.lang.String)
     */
    @Override
    protected void beforeSaving(String projectName) {
        this.projectName = projectName;
    }

    /*
     * @see eme.generator.saving.AbstractSavingStrategy#fileName()
     */
    @Override
    protected String getFileName() {
        return projectName + LocalDateTime.now().format(formatter);
    }

    /*
     * @see eme.generator.saving.AbstractSavingStrategy#filePath()
     */
    @Override
    protected String getFilePath() {
        return workspace.getRoot().getLocation().toFile().getPath() + SLASH + outputProjectName + SLASH + "model" + SLASH;
    }
}
