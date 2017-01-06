package eme.model;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.core.IPackageFragment;

/**
 * Is the representation of a package in the intermediate model.
 * @author Timur Saglam
 */
public class ExtractedPackage extends ExtractedElement {
    private final List<ExtractedClass> classes;
    private final List<ExtractedEnumeration> enumerations;
    private final List<ExtractedInterface> interfaces;
    private List<IPackageFragment> iPackageFragments;
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
        iPackageFragments = new LinkedList<IPackageFragment>();
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
    }

    /**
     * Adds an JDT representation to the package.
     * @param iPackageFragment is the new JDT representation.
     */
    public void addJDTRepresentation(IPackageFragment iPackageFragment) {
        iPackageFragments.add(iPackageFragment);
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
     * Getter for the JDT representations.
     * @return the JDT representations, an List<IPackageFragment>.
     */
    public List<IPackageFragment> getJDTRepresenations() {
        return iPackageFragments;
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
        List<ExtractedType> types = new LinkedList<>();
        types.addAll(classes);
        types.addAll(interfaces);
        types.addAll(enumerations);
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
        return classes.size() + interfaces.size() + enumerations.size() == 0;
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

    @Override
    public String toString() {
        if ("".equals(name)) {
            return "DEFAULT";
        }
        return getFullName();
    }
}
