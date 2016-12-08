package eme.model;

import java.util.LinkedList;
import java.util.List;

/**
 * Base class for an intermediate model.
 * @author Timur Saglam
 */
public class IntermediateModel {
    private String projectName;
    private ExtractedPackage rootElement;
    private List<ExtractedPackage> packages;

    /**
     * Basic constructor.
     */
    public IntermediateModel(String projectName) {
        packages = new LinkedList<ExtractedPackage>();
        this.projectName = projectName;
    }

    /**
     * Adds a new package to the intermediate model.
     * @param newPackage is the new package to add.
     */
    public void add(ExtractedPackage newPackage) {
        if (rootElement == null) { // if it is the first package
            rootElement = newPackage; // add as root
            newPackage.setAsRoot(); // mark as root
        } else {
            addToParent(newPackage);
        }
        packages.add(newPackage);
    }

    /**
     * Getter for the name of the project.
     * @return the name.
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * Getter for the root package of the model.
     * @return the root package.
     */
    public ExtractedPackage getRoot() {
        return rootElement;
    }

    /**
     * Prints the model.
     */
    public void print() {
        System.out.println(toString());
        System.out.println("   with packages " + packages.toString());
        // TODO (LOW) keep up to date.
    }

    @Override
    public String toString() {
        return "IntermediateModel[Packages=" + packages.size() + "]";
        // TODO (LOW) keep up to date.
    }

    /**
     * Adds a package to its parent by searching the parent in the list of packages.
     * @param newPackage is the package which is added to the parent package.
     */
    private void addToParent(ExtractedPackage newPackage) {
        for (ExtractedPackage oldPackage : packages) { // for all packages
            if (oldPackage.getFullName().equals(newPackage.getParent())) { // if parent
                oldPackage.addSubpackage(newPackage); // add to parent
                return; // can only have on parent
            }
        }
        throw new RuntimeException("Could not find package " + newPackage.getParent() + " to add package " + newPackage.getName() + " to.");
    }
}
