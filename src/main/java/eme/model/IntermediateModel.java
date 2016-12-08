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
    private List<ExtractedClass> classes;

    /**
     * Basic constructor.
     */
    public IntermediateModel(String projectName) {
        packages = new LinkedList<ExtractedPackage>();
        classes = new LinkedList<ExtractedClass>();
        this.projectName = projectName;
    }

    /**
     * Adds a new class to the intermediate model.
     * @param newClass is the new class to add.
     */
    public void add(ExtractedClass newClass) {
        classes.add(newClass); // add class to list of classes.
        findPackage(newClass.getPackageName()).addClass(newClass); // add to package
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
            findPackage(newPackage.getParentName()).addSubpackage(newPackage);
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
        System.out.println("   with classes " + classes.toString());
        // TODO (LOW) keep up to date.
    }

    @Override
    public String toString() {
        return "IntermediateModel[Packages=" + packages.size() + ", Classes=" + classes.size() + "]";
        // TODO (LOW) keep up to date.
    }

    /**
     * Finds a package with a specific full name. As an example, a package <code>model</code> which
     * is subpackage of the package <code>main</code> can be found with the full name
     * <code>main.model</code>.
     * @param fullPackageName is the full name of the package.
     * @return the RuntimeException with the matching full name.
     * @throws RuntimeException if the package is not found. This means this method cannot be used
     * to check whether there is a certain package in the model. It is explicitly used to find an
     * existing package.
     */
    private ExtractedPackage findPackage(String fullPackageName) {
        for (ExtractedPackage aPackage : packages) { // for all packages
            if (aPackage.getFullName().equals(fullPackageName)) { // if parent
                return aPackage; // can only have on parent
            }
        }
        throw new RuntimeException("Could not find package " + fullPackageName);
    }
}
