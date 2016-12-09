package eme.model;

import java.util.LinkedList;
import java.util.List;

/**
 * Is the representation of a package in the intermediate model.
 * @author Timur Saglam
 */
public class ExtractedPackage extends ExtractedElement {
    private List<ExtractedPackage> subpackages;
    private List<ExtractedClass> classes;
    private List<ExtractedInterface> interfaces;
    private List<ExtractedEnumeration> enumerations;
    protected boolean root;

    /**
     * Creates an extracted package.
     * @param fullName is the full name of the package.
     */
    public ExtractedPackage(String fullName) {
        super(fullName);
        subpackages = new LinkedList<ExtractedPackage>();
        classes = new LinkedList<ExtractedClass>();
        interfaces = new LinkedList<ExtractedInterface>();
        enumerations = new LinkedList<ExtractedEnumeration>();
        root = false;
    }

    /**
     * Adds a new class to the package.
     * @param newClass is the new class of the package.
     */
    public void add(ExtractedClass newClass) {
        classes.add(newClass);
    }

    /**
     * Adds a new enumeration to the package.
     * @param newInterface is the new interface of the package.
     */
    public void add(ExtractedEnumeration newEnumeration) {
        enumerations.add(newEnumeration);
    }

    /**
     * Adds a new interface to the package.
     * @param newInterface is the new interface of the package.
     */
    public void add(ExtractedInterface newInterface) {
        interfaces.add(newInterface);
    }

    /**
     * Adds a new subpackage to the package.
     * @param subpackage is the new subpackage of the package.
     */
    public void add(ExtractedPackage subpackage) {
        subpackages.add(subpackage);
    }

    /**
     * Getter for the classes.
     * @return the classes.
     */
    public List<ExtractedClass> getClasses() {
        return classes;
    }

    /**
     * Getter for the enumerations.
     * @return the enumerations.
     */
    public List<ExtractedEnumeration> getEnumerations() {
        return enumerations;
    }

    /**
     * Getter for the interfaces.
     * @return the interfaces.
     */
    public List<ExtractedInterface> getInterfaces() {
        return interfaces;
    }

    /**
     * Getter for the subpackages.
     * @return the subpackages.
     */
    public List<ExtractedPackage> getSubpackages() {
        return subpackages;
    }

    /**
     * Checks whether package is the root package.
     * @return
     */
    public boolean isRoot() {
        return root;
    }

    /**
     * Sets the package as root package, marking it as default package by changing its name.
     */
    public void setAsRoot() {
        root = true;
    }

    @Override
    public String toString() {
        return getFullName();
    }
}
