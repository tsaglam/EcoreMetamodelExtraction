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
    protected boolean root;

    /**
     * Creates an extracted package.
     * @param fullName is the full name of the package.
     */
    public ExtractedPackage(String fullName) {
        if (!fullName.contains(".")) { // top level package
            name = fullName; // normal name
            parent = ""; // but no parent package
        } else { // split name from parent package:
            int lastDot = fullName.lastIndexOf('.'); // index of dot that separates path and name
            name = fullName.substring(lastDot + 1); // get name
            parent = fullName.substring(0, lastDot); // get path
        }
        subpackages = new LinkedList<ExtractedPackage>();
        root = false;
    }

    /**
     * Adds a new subpackage to the package.
     * @param subpackage is the new subpackage of the package.
     */
    public void addSubpackage(ExtractedPackage subpackage) {
        subpackages.add(subpackage);
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
    public String getParent() {
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
