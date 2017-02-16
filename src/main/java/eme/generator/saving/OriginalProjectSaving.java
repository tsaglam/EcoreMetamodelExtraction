package eme.generator.saving;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;

/**
 * Saving strategy that saves the ecore file in the original project.
 * @author Timur Saglam
 */
public class OriginalProjectSaving extends AbstractSavingStrategy {
    protected String projectName;

    /**
     * Basic constructor.
     */
    public OriginalProjectSaving() {
        super(true); // refresh folder.
    }

    /*
     * @see eme.generator.saving.AbstractSavingStrategy#beforeSaving()
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
        return projectName;
    }

    /*
     * @see eme.generator.saving.AbstractSavingStrategy#filePath()
     */
    @Override
    protected String getFilePath() {
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        return workspace.getRoot().getLocation().toFile().getPath() + SLASH + projectName + SLASH + "model" + SLASH;
    }
}