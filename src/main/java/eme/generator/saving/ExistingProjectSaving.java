package eme.generator.saving;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;

/**
 * Saving Strategy that saves the ecore file in an existing Eclipse Project.
 * @author Timur Saglam
 */
public class ExistingProjectSaving extends AbstractSavingStrategy {
    private final DateTimeFormatter formatter;
    private final String outputProjectName;
    private String projectName;

    /**
     * Basic constructor.
     * @param outputProjectName is the name of the Eclipse project where models will be saved in.
     */
    public ExistingProjectSaving(String outputProjectName) {
        super(true);
        this.outputProjectName = outputProjectName;
        formatter = DateTimeFormatter.ofPattern("-yyyy-MM-dd-HH:mm.ss"); // date time format
        if (!projectExists(outputProjectName)) {
            throw new IllegalArgumentException("The specified output project could not be found: " + outputProjectName);
        }
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
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        return root.getLocation().toFile().getPath() + SLASH + outputProjectName + SLASH + "model" + SLASH;
    }
}
