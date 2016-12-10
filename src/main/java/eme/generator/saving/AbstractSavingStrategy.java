package eme.generator.saving;

import java.io.IOException;
import java.util.Collections;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

/**
 * This is the abstract super class for all saving strategies.
 * @author Timur Saglam
 */
public abstract class AbstractSavingStrategy {
    // TODO (MEDIUM) ideas for saving strategies: generator project, same project as source, create
    // new project
    protected String projectName;

    /**
     * Basic constructor. Takes the name of the project.
     * @param projectName is the name of the project where the metamodel was extracted.
     */
    public AbstractSavingStrategy(String projectName) {
        this.projectName = projectName;
    }

    /**
     * Saves an EPackage as an Ecore file.
     * @param ePackage is the EPackage to save.
     */
    public void save(EPackage ePackage) {
        String ecoreFilePath = projectPath() + "/" + projectName() + "/" + folder() + "/";
        String ecoreFileName = fileName();
        ePackage.eClass(); // Initialize the EPackage:
        ResourceSet resourceSet = new ResourceSetImpl(); // get new resource set
        Resource resource = null; // create a resource:
        try {
            resource = resourceSet.createResource(URI.createFileURI(ecoreFilePath + ecoreFileName + ".ecore"));
        } catch (IllegalArgumentException exception) {
            exception.printStackTrace();
        }
        resource.getContents().add(ePackage); // add the EPackage as root.
        try { // save the content:
            resource.save(Collections.EMPTY_MAP);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        refreshFolder(ecoreFilePath);
    }

    /**
     * Refreshes a specific folder in the Eclipse IDE
     * @param folderPath is the path of the folder.
     */
    private void refreshFolder(String folderPath) {
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        IContainer folder = root.getContainerForLocation(new Path(folderPath));
        try {
            folder.refreshLocal(IResource.DEPTH_INFINITE, null);
        } catch (CoreException exception) {
            System.err.println("Could not refresh output folder. Try that manually.");
            exception.printStackTrace(); // TODO (LOW) use message box.
        }
    }

    /**
     * Determines the path where the target project lies. Usually the workspace path.
     * @return the project path.
     */
    protected abstract String projectPath();

    /**
     * Determines the name of the target project.
     * @return the project name.
     */
    protected abstract String projectName();

    /**
     * Determines the target folder.
     * @return the target folder. Empty string if the project folder is the target folder.
     */
    protected abstract String folder();

    /**
     * Determines how the Ecore file will be named.
     * @return the file name.
     */
    protected abstract String fileName();
}
