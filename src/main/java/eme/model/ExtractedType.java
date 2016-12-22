package eme.model;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents a type in the intermediate model.
 * @author Timur Saglam
 */
public abstract class ExtractedType extends ExtractedElement {
    protected boolean innerType;
    protected String outerTypeName;
    protected String superClass;
    protected final List<String> superInterfaces;

    /**
     * Basic constructor.
     * @param fullName is the full name, containing name and package name.
     */
    public ExtractedType(String fullName) {
        super(fullName);
        superInterfaces = new LinkedList<String>();
        if (name.contains("$")) { // dollar in name means its a nested type
            innerType = true; // set nested true and adapt parent
            outerTypeName = name.substring(0, name.lastIndexOf('$'));
        }
    }

    /**
     * Adds an interface as super interface.
     * @param superInterface is the new super interface.
     */
    public void addSuperInterface(String superInterface) {
        superInterfaces.add(superInterface);
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ExtractedType) { // same class
            return getFullName().equals(((ExtractedType) obj).getFullName()); // same full name
        }
        return false;
    }

    /**
     * Returns the name of the outer type of an type, if that type is an inner type.
     * @return name of the outer type, null if the type is not an inner type.
     */
    public String getOuterTypeName() {
        return outerTypeName;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((getFullName() == null) ? 0 : getFullName().hashCode());
        return result;
    }

    /**
     * Method checks whether the type is a inner type.
     * @return true if it is a inner type.
     */
    public boolean isInnerType() {
        return innerType;
    }
}