package eme.model;

/**
 * Represents a class in the intermediate model.
 * @author Timur Saglam
 */
public class ExtractedClass extends ExtractedType {

    private final boolean isAbstract;

    /**
     * Basic constructor.
     * @param fullName is the full name, containing name and package name.
     * @param isAbstract determines whether the class is abstract or not.
     */
    public ExtractedClass(String fullName, boolean isAbstract) {
        super(fullName);
        this.isAbstract = isAbstract;
    }

    /**
     * Checks whether the class is abstract.
     * @return true if class is abstract.
     */
    public boolean isAbstract() {
        return isAbstract;
    }
    
    /**
     * Sets a class as super class.
     * @param superClass is the new super class.
     */
    public void setSuperClass(String superClass ) {
        this.superClass = superClass;
    }
}
