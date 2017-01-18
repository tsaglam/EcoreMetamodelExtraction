package eme.model;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Is the representation of a package in the intermediate model.
 * @author Timur Saglam
 */
public class ExtractedPackage extends ExtractedElement {
    private final List<ExtractedClass> classes;
    private final List<ExtractedEnumeration> enumerations;
    private final List<ExtractedInterface> interfaces;
    private final List<ExtractedType> types;
    private final List<ExtractedPackage> subpackages;
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
        types = new LinkedList<ExtractedType>();
        root = false;
    }

    /**
     * Adds a new subpackage to the package.
     * @param subpackage is the new subpackage of the package.
     */
    public void add(ExtractedPackage subpackage) {
        subpackages.add(subpackage);
    }

    /**
     * Adds a new type to the package.
     * @param type is the new type of the package.
     */
    public void add(ExtractedType type) {
        if (type.getClass() == ExtractedClass.class) {
            classes.add((ExtractedClass) type);
        } else if (type.getClass() == ExtractedInterface.class) {
            interfaces.add((ExtractedInterface) type);
        } else if (type.getClass() == ExtractedEnumeration.class) {
            enumerations.add((ExtractedEnumeration) type);
        }
        types.add(type);
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
     * Getter for the types (interfaces, classes and enumerations).
     * @return the types.
     */
    public List<ExtractedType> getTypes() {
        return types;
    }

    /**
     * Checks whether package is empty.
     * @return true if the package is empty.
     */
    public boolean isEmpty() {
        for (ExtractedPackage subpackage : subpackages) {
            if (!subpackage.isEmpty()) {
                return false;
            }
        }
        return types.size() == 0;
    }

    /**
     * Checks whether package is the root package.
     * @return true if the package is the root package.
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

    /**
     * Sorts the content of the package. Sorts its types, its subpackages and all the content of every subpackage.
     */
    public void sort() {
        Collections.sort(interfaces);
        Collections.sort(classes);
        Collections.sort(enumerations);
        Collections.sort(types);
        Collections.sort(subpackages);
        for (ExtractedPackage subpackage : subpackages) {
            subpackage.sort(); // sort the content of alles subpackages.
        }
    }

    @Override
    public String toString() {
        if ("".equals(name)) {
            return "DEFAULT";
        }
        return getFullName();
    }
}
