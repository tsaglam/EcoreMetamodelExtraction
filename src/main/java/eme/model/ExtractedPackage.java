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
    private String packagePath; // names of all packages above this package in the hierarchy
    private String prefix;
    private String uri;
    private List<ExtractedPackage> subpackages;

    /**
     * Creates an extracted package.
     * @param fullName is the full name of the package.
     */
    public ExtractedPackage(String fullName) {
        if (fullName.equals("")) { // TODO make this better
            name = "";
            packagePath = "";
        } else if (!fullName.contains(".")) {
            name = fullName;
            packagePath = "";
        } else {
            int lastDot = fullName.lastIndexOf('.'); // index of dot that separates path and name
            name = fullName.substring(lastDot + 1); // get name
            packagePath = fullName.substring(0, lastDot); // get path
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
        ePackage.setName(name);
        ePackage.setNsPrefix(prefix);
        ePackage.setNsURI(uri);
        return ePackage;
    }

    /**
     * Getter for the full package name.
     * @return the full name of the package, consisting out of the package path and the package name
     * separated by an dot.
     */
    public String getFullName() {
        if (packagePath.equals("")) {
            return name;
        }
        return packagePath + "." + name;
    }

    /**
     * Getter for the package name.
     * @return the package name.
     */
    public String getName() {
        return name;
    }

    /**
     * Getter for the package path.
     * @return the package path.
     */
    public String getPath() {
        return packagePath;
    }

    /**
     * Sets the package as root package, marking it as default package by changing its name.
     */
    public void setAsRoot() {
        name += "DEFAULT";
    }

    @Override
    public String toString() {
        return getFullName();
    }
}
