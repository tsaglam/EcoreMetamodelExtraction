package eme.model;

import java.util.LinkedList;
import java.util.List;

/**
 * Is the representation of a package in the intermediate model.
 * @author Timur Saglam
 */
public class ExtractedPackage extends ExtractedElement {
    private String name; // name of the package
    private String parent; // full name of parent package
    private List<ExtractedPackage> subpackages;
    private List<ExtractedClass> classes;
    protected boolean root;

    /**
     * Creates an extracted package.
     * @param fullName is the full name of the package.
     */
    public ExtractedPackage(String fullName) {
        name = createName(fullName);
        parent = createPath(fullName);
        subpackages = new LinkedList<ExtractedPackage>();
        classes = new LinkedList<ExtractedClass>();
        root = false;
    }

    /**
     * Adds a new class to the package.
     * @param newClass is the new class of the package.
     */
    public void addClass(ExtractedClass newClass) {
        classes.add(newClass);
    }

    /**
     * Adds a new subpackage to the package.
     * @param subpackage is the new subpackage of the package.
     */
    public void addSubpackage(ExtractedPackage subpackage) {
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
     * Getter for the full package name.
     * @return the full name of the package, consisting out of the package path and the package name
     * separated by an dot.
     */
    public String getFullName() {
        if (parent.equals("")) {
            return name;
        }
        return parent + "." + name;
    }

    /**
     * Getter for the package name.
     * @return the package name.
     */
    public String getName() {
        return name;
    }

    /**
     * Getter for the name of the packages parent.
     * @return the parent name.
     */
    public String getParentName() {
        return parent;
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
