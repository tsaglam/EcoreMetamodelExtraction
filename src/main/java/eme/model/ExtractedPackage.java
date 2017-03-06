package eme.model;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Is the representation of a package in the {@link IntermediateModel}.
 * @author Timur Saglam
 */
public class ExtractedPackage extends ExtractedElement {
    private final List<ExtractedClass> classes;
    private final List<ExtractedEnum> enumerations;
    private final List<ExtractedInterface> interfaces;
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
        enumerations = new LinkedList<ExtractedEnum>();
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
     * Adds a new {@link ExtractedType} to the package.
     * @param type is the new {@link ExtractedType} of the package.
     */
    public void add(ExtractedType type) {
        if (type.getClass() == ExtractedClass.class) {
            classes.add((ExtractedClass) type);
        } else if (type.getClass() == ExtractedInterface.class) {
            interfaces.add((ExtractedInterface) type);
        } else if (type.getClass() == ExtractedEnum.class) {
            enumerations.add((ExtractedEnum) type);
        }
    }

    /**
     * accessor for the {@link ExtractedClass}es.
     * @return the classes.
     */
    public List<ExtractedClass> getClasses() {
        return classes;
    }

    /**
     * accessor for the {@link ExtractedEnum}s.
     * @return the enumerations.
     */
    public List<ExtractedEnum> getEnumerations() {
        return enumerations;
    }

    /**
     * accessor for the {@link ExtractedInterface}s.
     * @return the interfaces.
     */
    public List<ExtractedInterface> getInterfaces() {
        return interfaces;
    }

    /**
     * accessor for the subpackages.
     * @return the subpackages.
     */
    public List<ExtractedPackage> getSubpackages() {
        return subpackages;
    }

    /**
     * accessor for the {@link ExtractedType}s (interfaces, classes and enumerations).
     * @return the types.
     */
    public List<ExtractedType> getTypes() {
        List<ExtractedType> types = new LinkedList<ExtractedType>(enumerations);
        types.addAll(classes);
        types.addAll(interfaces);
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
        return getTypes().isEmpty();
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
