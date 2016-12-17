package eme.generator.saving;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;

import eme.generator.EMFProjectGenerator;

/**
 * Saving strategy that creates a new project for very saved model.
 * @author Timur Saglam
 */
public class NewProjectSavingStrategy extends AbstractSavingStrategy {
    private String generatedProjectName;
    private String fileName;

    /**
     * Basic constructor.
     */
    public NewProjectSavingStrategy() {
        super(true);
    }

    /*
     * @see eme.generator.saving.AbstractSavingStrategy#beforeSaving()
     */
    @Override
    protected void beforeSaving(String projectName) {
        fileName = projectName;
        IProject newProject = EMFProjectGenerator.createProject(projectName + "Model");
        this.generatedProjectName = newProject.getName(); //
    }

    /*
     * @see eme.generator.saving.AbstractSavingStrategy#filePath()
     */
    @Override
    protected String filePath() {
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        return workspace.getRoot().getLocation().toFile().getPath() + "/" + generatedProjectName + "/model/";
    }

    /*
     * @see eme.generator.saving.AbstractSavingStrategy#fileName()
     */
    @Override
    protected String fileName() {
        return fileName;
    }

}
