package eme.generator.saving;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

/**
 * This is the abstract super class for all saving strategies.
 * @author Timur Saglam
 */
public abstract class AbstractSavingStrategy {
    private static final Logger logger = LogManager.getLogger(AbstractSavingStrategy.class.getName());
    private final boolean saveInProject;
    protected static final char SLASH = File.separatorChar;
    /**
     * Basic constructor. Takes the name of the project.
     * @param saveInProject determines whether the folder where the file is saved should be refreshed in the Eclipse
     * IDE. Set this true if the file is saved in a project in the IDE.
     */
    public AbstractSavingStrategy(boolean saveInProject) {
        this.saveInProject = saveInProject;
    }

    /**
     * Saves an {@link EPackage} as an Ecore file. The method calls the methods filePath() and fileName() to get the
     * information it needs to save the metamodel. If the default saving behavior is not wanted, this method has to be
     * overridden in the strategy class that overrides this class.
     * @param ePackage is the EPackage to save.
     * @param projectName is the name of the project the EPAckage was generated from.
     * @return the saving information.
     */
    public SavingInformation save(EPackage ePackage, String projectName) {
        beforeSaving(projectName);
        ePackage.eClass(); // Initialize the EPackage:
        Resource.Factory.Registry registry = Resource.Factory.Registry.INSTANCE;
        Map<String, Object> map = registry.getExtensionToFactoryMap();
        map.put(EcorePackage.eNAME, new XMIResourceFactoryImpl());  // add default extension
        ResourceSet resourceSet = new ResourceSetImpl(); // get new resource set
        Resource resource = null; // create a resource:
        String fileName = getFileName(); // get name
        String filePath = getFilePath(); // get path
        try {
            resource = resourceSet.createResource(URI.createFileURI(filePath + fileName + ".ecore"));
        } catch (IllegalArgumentException exception) {
            logger.error("Error while saving the metamodel.", exception);
        }
        resource.getContents().add(ePackage); // add the EPackage as root.
        try { // save the content:
            resource.save(Collections.EMPTY_MAP);
        } catch (IOException exception) {
            logger.error("Error while saving the metamodel.", exception);
        }
        if (saveInProject) {
            refreshFolder(getFilePath());
        }
        logger.info("The extracted metamodel was saved under: " + getFilePath());
        return new SavingInformation(filePath, fileName);
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
            logger.warn("Could not refresh output folder. Try that manually.", exception);
        }
    }

    /**
     * Can be used to prepare the saving itself.
     * @param projectName is the name of the project where the metamodel was extracted.
     */
    protected abstract void beforeSaving(String projectName);

    /**
     * Returns the project suffix, which basically is a separator character and a suffix base, depending on the naming
     * type of the projects name (e.g. "My-Project" and "model" will return "-Model").
     * @param projectName is the projects name. Determines which separator is used and decides whether there is
     * capitalization.
     * @param base is the base string of the suffix.
     * @return the project suffix.
     */
    protected String createSuffix(String projectName, String base) {
        char[] candidates = { ' ', '.', '-', '_', ':' }; // possible separators
        char separator = Character.MIN_VALUE; // 0000
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
        if (!projectName.matches(".*[A-Z].*")) { // if has no upper case
            base = base.toLowerCase(); // use lower case suffix
        }
        if (separator == Character.MIN_VALUE) { // no separator was chosen
            return base; // return suffix without separator
        }
        return separator + base; // identifier = separator + suffix
    }

    /**
     * Determines the name of the ecore file.
     * @return the file name.
     */
    protected abstract String getFileName();

    /**
     * Determines the path where the ecore file is saved.
     * @return the file path.
     */
    protected abstract String getFilePath();

    /**
     * Check whether the output project exists.
     * @param name is the name of the project.
     * @return true if it exists.
     */
    protected boolean projectExists(String name) {
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        for (IProject iProject : root.getProjects()) {
            if (iProject.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }
}