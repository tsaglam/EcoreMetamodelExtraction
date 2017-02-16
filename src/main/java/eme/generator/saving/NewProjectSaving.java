package eme.generator.saving;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;

import eme.generator.EMFProjectGenerator;

/**
 * Saving strategy that creates a new project for every saved ecore file.
 * @author Timur Saglam
 */
public class NewProjectSaving extends AbstractSavingStrategy {
    private String fileName;
    private String generatedProjectName;

    /**
     * Basic constructor.
     */
    public NewProjectSaving() {
        super(true);
    }

    /*
     * @see eme.generator.saving.AbstractSavingStrategy#beforeSaving()
     */
    @Override
    protected void beforeSaving(String projectName) {
        fileName = projectName;
        IProject newProject = EMFProjectGenerator.createProject(projectName + createSuffix(projectName, "Model"));
        this.generatedProjectName = newProject.getName();
    }

    /*
     * @see eme.generator.saving.AbstractSavingStrategy#fileName()
     */
    @Override
    protected String getFileName() {
        return fileName;
    }

    /*
     * @see eme.generator.saving.AbstractSavingStrategy#filePath()
     */
    @Override
    protected String getFilePath() {
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        return workspace.getRoot().getLocation().toFile().getPath() + SLASH + generatedProjectName + SLASH + "model" + SLASH;
    }
}