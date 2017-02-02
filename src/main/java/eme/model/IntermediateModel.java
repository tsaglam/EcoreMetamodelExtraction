package eme.model;

import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * Base class for an intermediate model. The intermediate model is the temporary model between the implicit model of the
 * code files and the Ecore metamodel.
 * @author Timur Saglam
 */
public class IntermediateModel {
    private static final Logger logger = LogManager.getLogger(IntermediateModel.class.getName());
    private final Set<ExtractedType> externalTypes;
    private final Set<ExtractedPackage> packages;
    private final String projectName;
    private ExtractedPackage rootPackage;
    private final Set<ExtractedType> types;

    /**
     * Basic constructor.
     * @param projectName is the name of the project the model was extracted from.
     */
    public IntermediateModel(String projectName) {
        packages = new LinkedHashSet<ExtractedPackage>();
        types = new LinkedHashSet<ExtractedType>();
        externalTypes = new LinkedHashSet<ExtractedType>();
        this.projectName = projectName;
    }

    /**
     * Adds a new {@link ExtractedPackage} to the intermediate model if it is not already added.
     * @param newPackage is the new {@link ExtractedPackage} to add.
     */
    public void add(ExtractedPackage newPackage) {
        if (packages.add(newPackage)) {
            if (rootPackage == null) { // if it is the first package
                rootPackage = newPackage; // add as root
                newPackage.setAsRoot(); // mark as root
            } else {
                getPackage(newPackage.getParentName()).add(newPackage);
            }
        }
    }

    /**
     * Adds a new {@link ExtractedType} to the intermediate model if it is not already added. Finds parent
     * {@link ExtractedPackage} automatically.
     * @param type is the new {@link ExtractedType} to add.
     */
    public void add(ExtractedType type) {
        addTo(type, getPackage(type.getParentName()));
    }

    /**
     * Adds a new external {@link ExtractedType} to the intermediate model.
     * @param type is the new external type to add.
     */
    public void addExternal(ExtractedType type) {
        externalTypes.add(type);
    }

    /**
     * Adds a new {@link ExtractedType} to the intermediate model and to a specific parent {@link ExtractedPackage} if
     * it is not already added.
     * @param type is the new {@link ExtractedType} to add.
     * @param parent is the parent {@link ExtractedPackage}.
     */
    public void addTo(ExtractedType type, ExtractedPackage parent) {
        if (types.add(type)) { // add class to list of classes.
            parent.add(type);
        }
    }

    /**
     * Checks whether the model contains an {@link ExtractedType} whose full name matches a given full name.
     * @param fullName is the given full name.
     * @return true if it contains the {@link ExtractedType}, false if not.
     */
    public boolean contains(String fullName) {
        return getType(fullName) != null;
    }

    /**
     * Checks whether the model contains an external {@link ExtractedType} whose full name matches a given full name.
     * @param fullName is the given full name.
     * @return true if it contains the external {@link ExtractedType}, false if not.
     */
    public boolean containsExternal(String fullName) {
        return getExternalType(fullName) != null;
    }

    /**
     * Returns the external {@link ExtractedType} of the intermediate model whose full name matches the given full name.
     * @param fullName is the given full name.
     * @return the external {@link ExtractedType} with the matching name or null if the name is not found.
     */
    public ExtractedType getExternalType(String fullName) {
        return getTypeFrom(fullName, externalTypes);
    }

    /**
     * Returns all the external {@link ExtractedType}s of the intermediate model.
     * @return the set of external {@link ExtractedType}s.
     */
    public Set<ExtractedType> getExternalTypes() {
        return externalTypes;
    }

    /**
     * Returns the {@link ExtractedPackage} of the intermediate model whose full name matches the given full name.
     * @param fullName is the given full name.
     * @return the {@link ExtractedPackage} with the matching name.
     * @throws RuntimeException if the {@link ExtractedPackage} is not found. This means this method cannot be used to
     * check whether there is a certain package in the model. It is explicitly used to find an existing package.
     */
    public ExtractedPackage getPackage(String fullName) {
        for (ExtractedPackage aPackage : packages) { // for all packages
            if (aPackage.getFullName().equals(fullName)) { // if parent
                return aPackage; // can only have on parent
            }
        }
        throw new IllegalArgumentException("Could not find package " + fullName);
    }

    /**
     * accessor for the name of the project.
     * @return the name.
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * accessor for the root {@link ExtractedPackage} of the model.
     * @return the root package.
     */
    public ExtractedPackage getRoot() {
        return rootPackage;
    }

    /**
     * Returns the {@link ExtractedType} of the intermediate model whose full name matches the given full name.
     * @param fullName is the given full name.
     * @return the {@link ExtractedType} with the matching name or null if the name is not found.
     */
    public ExtractedType getType(String fullName) {
        return getTypeFrom(fullName, types);
    }

    /**
     * Prints the model.
     */
    public void print() {
        logger.info(toString());
        logger.info("   with packages " + packages.toString());
        logger.info("   with types " + types.toString());
        logger.info("   with external types " + externalTypes.toString());
        // TODO (LOW) keep up to date.
    }

    /**
     * Sorts the content of the root {@link ExtractedPackage}. Sorts its types, its subpackages and all the content of
     * every subpackage.
     */
    public void sort() {
        rootPackage.sort();
    }

    @Override
    public String toString() {
        return "IntermediateModel of " + projectName + ": [Packages=" + packages.size() + ", Types=" + types.size() + ", ExternalTypes="
                + externalTypes.size() + "]";
        // TODO (LOW) keep up to date.
    }

    /**
     * Finds {@link ExtractedType} from set of {@link ExtractedType}s by its full name.
     * @param fullName is full name.
     * @param typeSet is the set of {@link ExtractedType}s.
     * @return the {@link ExtractedType} with the matching name or null if the name is not found.
     */
    private ExtractedType getTypeFrom(String fullName, Set<ExtractedType> typeSet) {
        for (ExtractedType type : typeSet) { // for all packages
            if (type.getFullName().equals(fullName)) { // if parent
                return type; // can only have on parent
            }
        }
        return null;
    }
}