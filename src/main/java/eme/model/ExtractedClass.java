package eme.model;

import org.eclipse.jdt.core.IType;

/**
 * Represents a class in the intermediate model.
 * @author Timur Saglam
 */
public class ExtractedClass extends ExtractedType {

    private final boolean abstractClass;

    /**
     * Basic constructor.
     * @param fullName is the full name, containing name and package name.
     * @param isAbstract determines whether the class is abstract or not.
     */
    public ExtractedClass(String fullName, boolean isAbstract) {
        super(fullName, null);
        abstractClass = isAbstract;
    }

    /**
     * Constructor that also takes the JDT representation.
     * @param fullName is the full name, containing name and package name.
     * @param isAbstract determines whether the class is abstract or not.
     * @param iType is the JDT representation.
     */
    public ExtractedClass(String fullName, boolean isAbstract, IType iType) {
        super(fullName, iType);
        abstractClass = isAbstract;
    }

    /**
     * Getter for the name of the super class.
     * @return the super class name.
     */
    public String getSuperClass() {
        return superClass;
    }

    /**
     * Checks whether the class is abstract.
     * @return true if class is abstract.
     */
    public boolean isAbstract() {
        return abstractClass;
    }

    /**
     * Sets a class as super class.
     * @param superClass is the new super class.
     */
    public void setSuperClass(String superClass) {
        this.superClass = superClass;
    }
}
