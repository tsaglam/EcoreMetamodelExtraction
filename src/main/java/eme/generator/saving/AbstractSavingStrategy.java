package eme.generator.saving;

import java.io.IOException;
import java.util.Collections;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

/**
 * This is the abstract super class for all saving startegies.
 * @author Timur Saglam
 */
public abstract class AbstractSavingStrategy {

    protected String projectName;

    /**
     * Basic
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
