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
    private String projectName;

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
        IProject project = EMFProjectGenerator.createProject(projectName + "Model");
        this.projectName = project.getName();
    }

    /*
     * @see eme.generator.saving.AbstractSavingStrategy#filePath()
     */
    @Override
    protected String filePath() {
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        return workspace.getRoot().getLocation().toFile().getPath() + "/" + projectName + "/model/";
    }

    /*
     * @see eme.generator.saving.AbstractSavingStrategy#fileName()
     */
    @Override
    protected String fileName() {
        return projectName;
    }

}
