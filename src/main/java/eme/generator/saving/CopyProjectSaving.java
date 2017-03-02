package eme.generator.saving;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;

/**
 * Saving strategy that copies the original project and saves the ecore file in the copy.
 * @author Timur Saglam
 */
public class CopyProjectSaving extends OriginalProjectSaving {
    private static final Logger logger = LogManager.getLogger(CopyProjectSaving.class.getName());
    private IProject projectCopy;
    final private String projectSuffix;

    /**
     * Basic constructor.
     * @param projectSuffix is the suffix of the name of the copied project.
     */
    public CopyProjectSaving(String projectSuffix) {
        super(); // refresh folder.
        this.projectSuffix = projectSuffix;
    }

    /**
     * Copies an specific {@link IProject}.
     * @param project is the specific {@link IProject} to copy to.
     * @return the copy of the original {@link IProject}.
     */
    private IProject copy(IProject project) {
        IProject copy = null;
        try {
            IPath newPath = createPath(project);
            project.copy(newPath, false, new NullProgressMonitor());
            IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
            copy = workspaceRoot.getProject(newPath.toString());
        } catch (CoreException exception) {
            logger.fatal(exception);
        }
        return copy;
    }

    /**
     * Creates the {@link IPath} of the copy.
     */
    private IPath createPath(IProject project) {
        String suffix = createSuffix(project.getName(), projectSuffix);
        int version = 2;
        String versionString = "";
        while (projectExists(project.getName() + suffix + versionString)) { // to avoid duplicates:
            versionString = Integer.toString(version);
            version++;
        }
        return new Path(project.getFullPath() + suffix + versionString);
    }

    /**
     * Gets {@link IProject} from name.
     */
    private IProject getProject(String projectName) {
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        for (IProject project : root.getProjects()) {
            if (project.getName().equals(projectName)) {
                return project;
            }
        }
        return null;
    }

    /*
     * @see eme.generator.saving.AbstractSavingStrategy#beforeSaving(java.lang.String)
     */
    @Override
    protected void beforeSaving(String projectName) {
        super.projectName = projectName;
        IProject project = getProject(projectName);
        projectCopy = copy(project);
    }

    /*
     * @see eme.generator.saving.AbstractSavingStrategy#filePath()
     */
    @Override
    protected String getFilePath() {
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        return workspace.getRoot().getLocation().toFile().getPath() + SLASH + projectCopy.getName() + SLASH + "model" + SLASH;
    }
}