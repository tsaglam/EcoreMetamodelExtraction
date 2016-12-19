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
    private final boolean saveInProject;

    /**
     * Basic constructor. Takes the name of the project.
     * @param saveInProject determines whether the folder where the file is saved should be
     * refreshed in the Eclipse IDE. Set this true if the file is saved in a project in the IDE.
     */
    public AbstractSavingStrategy(boolean saveInProject) {
        this.saveInProject = saveInProject;
    }

    /**
     * Saves an EPackage as an Ecore file. The method calls the methods filePath() and fileName() to
     * get the information it needs to save the metamodel. If the default saving behavior is not
     * wanted, this method has to be overridden in the strategy class that overrides this class.
     * @param ePackage is the EPackage to save.
     * @param projectName is the name of the project the EPAckage was generated from.
     */
    public void save(EPackage ePackage, String projectName) {
        beforeSaving(projectName);
        ePackage.eClass(); // Initialize the EPackage:
        ResourceSet resourceSet = new ResourceSetImpl(); // get new resource set
        Resource resource = null; // create a resource:
        try {
            resource = resourceSet.createResource(URI.createFileURI(filePath() + fileName() + ".ecore"));
        } catch (IllegalArgumentException exception) {
            exception.printStackTrace();
        }
        resource.getContents().add(ePackage); // add the EPackage as root.
        try { // save the content:
            resource.save(Collections.EMPTY_MAP);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        if (saveInProject) {
            refreshFolder(filePath());
        }
        System.out.println("The extracted metamodel was saved under: " + filePath());
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
            exception.printStackTrace();
        }
    }

    /**
     * Can be used to prepare the saving itself.
     * @param projectName is the name of the project where the metamodel was extracted.
     */
    protected abstract void beforeSaving(String projectName);

    /**
     * Determines the path where the ecore file is saved.
     * @return the file path.
     */
    protected abstract String filePath();

    /**
     * Determines the name of the ecore file.
     * @return the file name.
     */
    protected abstract String fileName();
}
