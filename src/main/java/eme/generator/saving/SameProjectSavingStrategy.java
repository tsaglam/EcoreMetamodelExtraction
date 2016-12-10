package eme.generator.saving;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;

/**
 * Saving strategy that saves the ecore file in the same project where it was extracted from.
 * @author Timur Saglam
 */
public class SameProjectSavingStrategy extends AbstractSavingStrategy {
    private String projectName;

    /**
     * Basic constructor.
     */
    public SameProjectSavingStrategy() {
        super(true); // refresh folder.
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

    /*
     * @see eme.generator.saving.AbstractSavingStrategy#beforeSaving()
     */
    @Override
    protected void beforeSaving(String projectName) {
        this.projectName = projectName;
    }
}