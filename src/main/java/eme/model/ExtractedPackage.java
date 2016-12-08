package eme.model;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

/**
 * Is the representation of a package in the intermediate model.
 * @author Timur Saglam
 */
public class ExtractedPackage extends ExtractedElement {
    private String name; // name of the package
    private String parent; // full name of parent package
    private List<ExtractedPackage> subpackages;

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
    }

    /**
     * Adds a new subpackage to the package.
     * @param subpackage is the new subpackage of the package.
     */
    public void addSubpackage(ExtractedPackage subpackage) {
        subpackages.add(subpackage);
    }

    /**
     * @see eme.model.ExtractedElement#generateEcoreRepresentation()
     */
    @Override
    public EObject generateEcoreRepresentation() {
        EPackage ePackage = ecoreFactory.createEPackage();
        if (root = true) {
            ePackage.setName("DEFAULT");
            ePackage.setNsPrefix("DEFAULT");
            ePackage.setNsURI("http://www.eme.org/");
        } else {
            ePackage.setName(name);
        }
        return ePackage;
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

    @Override
    public String toString() {
        return getFullName();
    }
}
