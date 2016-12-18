package eme.model;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Base class for an intermediate model.
 * @author Timur Saglam
 */
public class IntermediateModel {
    private Set<ExtractedClass> classes;
    private Set<ExtractedEnumeration> enumerations;
    private Set<ExtractedInterface> interfaces;
    private Set<ExtractedPackage> packages;
    private String projectName;
    private ExtractedPackage rootElement;

    /**
     * Basic constructor.
     * @param projectName is the name of the project the model was extracted from.
     */
    public IntermediateModel(String projectName) {
        packages = new LinkedHashSet<ExtractedPackage>();
        classes = new LinkedHashSet<ExtractedClass>();
        interfaces = new LinkedHashSet<ExtractedInterface>();
        enumerations = new LinkedHashSet<ExtractedEnumeration>();
        this.projectName = projectName;
    }

    /**
     * Adds a new class to the intermediate model. Finds parent package automatically.
     * @param newClass is the new class to add.
     */
    public void add(ExtractedClass newClass) {
        addTo(newClass, findParent(newClass));
    }

    /**
     * Adds a new enumeration to the intermediate model. Finds parent package automatically.
     * @param newEnum is the new enumeration to add.
     */
    public void add(ExtractedEnumeration newEnum) {
        addTo(newEnum, findParent(newEnum));
    }

    /**
     * Adds a new class to the intermediate model. Finds parent package automatically.
     * @param newInterface is the new class to add.
     */
    public void add(ExtractedInterface newInterface) {
        addTo(newInterface, findParent(newInterface));
    }

    /**
     * Adds a new package to the intermediate model.
     * @param newPackage is the new package to add.
     */
    public void add(ExtractedPackage newPackage) {
        if (packages.add(newPackage)) {
            if (rootElement == null) { // if it is the first package
                rootElement = newPackage; // add as root
                newPackage.setAsRoot(); // mark as root
            } else {
                getPackage(newPackage.getParentName()).add(newPackage);
            }
        }
    }

    /**
     * Adds a new class to the intermediate model and to a specific parent package.
     * @param newClass is the new class to add.
     * @param parent is the parent package.
     */
    public void addTo(ExtractedClass newClass, ExtractedPackage parent) {
        if (classes.add(newClass)) { // add class to list of classes.
            parent.add(newClass);
        }
    }

    /**
     * Adds a new enumeration to the intermediate model and to a specific parent package.
     * @param newEnum is the new enumeration to add.
     * @param parent is the parent package.
     */
    public void addTo(ExtractedEnumeration newEnum, ExtractedPackage parent) {
        if (enumerations.add(newEnum)) { // add class to list of classes.
            parent.add(newEnum);
        }
    }

    /**
     * Adds a new class to the intermediate model and to a specific parent package.
     * @param newInterface is the new class to add.
     * @param parent is the parent package.
     */
    public void addTo(ExtractedInterface newInterface, ExtractedPackage parent) {
        if (interfaces.add(newInterface)) { // add class to list of classes.
            parent.add(newInterface);
        }
    }

    /**
     * Returns the package of the intermediate model whose full name matches the given full name.
     * @param fullName is the given full name.
     * @return the package with the matching name.
     * @throws RuntimeException if the package is not found. This means this method cannot be used
     * to check whether there is a certain package in the model. It is explicitly used to find an
     * existing package.
     */
    public ExtractedPackage getPackage(String fullName) {
        for (ExtractedPackage aPackage : packages) { // for all packages
            if (aPackage.getFullName().equals(fullName)) { // if parent
                return aPackage; // can only have on parent
            }
        }
        throw new RuntimeException("Could not find package " + fullName);
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
        return projectName + "IntermediateModel[Packages=" + packages.size() + ", Classes=" + classes.size() + ", Interfaces=" + interfaces.size()
                + ", Enums=" + enumerations.size() + "]";
        // TODO (LOW) keep up to date.
    }

    /**
     * Finds a package for a specific ExtractedElement according to its parents name.
     * @param element is the ExtractedElement.
     * @return the package with the matching full name.
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