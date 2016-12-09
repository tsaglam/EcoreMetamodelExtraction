package eme.model;

import java.util.LinkedList;
import java.util.List;

/**
 * Base class for an intermediate model.
 * @author Timur Saglam
 */
public class IntermediateModel {
    private List<ExtractedClass> classes;
    private List<ExtractedEnumeration> enumerations;
    private List<ExtractedInterface> interfaces;
    private List<ExtractedPackage> packages;
    private String projectName;
    private ExtractedPackage rootElement;

    /**
     * Basic constructor.
     */
    public IntermediateModel(String projectName) {
        packages = new LinkedList<ExtractedPackage>();
        classes = new LinkedList<ExtractedClass>();
        interfaces = new LinkedList<ExtractedInterface>();
        enumerations = new LinkedList<ExtractedEnumeration>();
        this.projectName = projectName;
    }

    /**
     * Adds a new class to the intermediate model.
     * @param newClass is the new class to add.
     */
    public void add(ExtractedClass newClass) {
        classes.add(newClass); // add class to list of classes.
        findParent(newClass).add(newClass); // add to package
    }

    /**
     * Adds a new enumeration to the intermediate model.
     * @param newEnum is the new enumeration to add.
     */
    public void add(ExtractedEnumeration newEnum) {
        enumerations.add(newEnum); // add class to list of classes.
        findParent(newEnum).add(newEnum); // add to package
    }

    /**
     * Adds a new class to the intermediate model.
     * @param newInterface is the new class to add.
     */
    public void add(ExtractedInterface newInterface) {
        interfaces.add(newInterface); // add class to list of classes.
        findParent(newInterface).add(newInterface); // add to package
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
            findParent(newPackage).add(newPackage);
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
        System.out.println("   with interfaces " + interfaces.toString());
        System.out.println("   with enumerations " + enumerations.toString());
        // TODO (LOW) keep up to date.
    }

    @Override
    public String toString() {
        return "IntermediateModel[Packages=" + packages.size() + ", Classes=" + classes.size() + ", Interfaces=" + interfaces.size() + ", Enums="
                + enumerations.size() + "]";
        // TODO (LOW) keep up to date.
    }

    /**
     * Finds a package for a specific ExtractedElement according to its parents name.
     * @param element is the ExtractedElement.
     * @return the package with the matching full name.
     * @throws RuntimeException if the package is not found. This means this method cannot be used
     * to check whether there is a certain package in the model. It is explicitly used to find an
     * existing package.
     */
    private ExtractedPackage findParent(ExtractedElement element) {
        String parent = element.getParentName();
        for (ExtractedPackage aPackage : packages) { // for all packages
            if (aPackage.getFullName().equals(parent)) { // if parent
                return aPackage; // can only have on parent
            }
        }
        throw new RuntimeException("Could not find package " + parent);
    }
}
