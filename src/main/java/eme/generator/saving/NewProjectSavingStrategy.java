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

    /**
     * Returns the project identifier, which basically is a separator character and the String
     * "Model" or "Model", depending on the naming type of the projects name.
     * @param projectName is the projects name. determines which identifier is used.
     * @return the project identifier.
     */
    private String identifier(String projectName) {
        char[] candidates = { ' ', '.', '-', '_', ':' }; // possible separators
        char separator = Character.MIN_VALUE; // no char (empty string) is default
        int max = 0;
        for (char candidate : candidates) { // for every candidate
            int ctr = 0; // count occurrences in project name:
            for (int i = 0; i < projectName.length(); i++) {
                ctr = (candidate == projectName.charAt(i)) ? (ctr + 1) : ctr;
            }
            if (ctr > max) { // if candidate is new most used candidate
                max = ctr; // set as new preferred separator
                separator = candidate;
            }
        }
        String suffix = "Model"; // default suffix
        if (!projectName.matches(".*[A-Z].*")) { // if has no upper case
            suffix = suffix.toLowerCase(); // use lower case suffix
        }
        return separator + suffix; // identifier = separator + suffix
    }

    /*
     * @see eme.generator.saving.AbstractSavingStrategy#beforeSaving()
     */
    @Override
    protected void beforeSaving(String projectName) {
        fileName = projectName;
        IProject newProject = EMFProjectGenerator.createProject(projectName + identifier(projectName));
        this.generatedProjectName = newProject.getName(); //
    }

    /*
     * @see eme.generator.saving.AbstractSavingStrategy#fileName()
     */
    @Override
    protected String fileName() {
        return fileName;
    }

    /*
     * @see eme.generator.saving.AbstractSavingStrategy#filePath()
     */
    @Override
    protected String filePath() {
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        return workspace.getRoot().getLocation().toFile().getPath() + "/" + generatedProjectName + "/model/";
    }
}