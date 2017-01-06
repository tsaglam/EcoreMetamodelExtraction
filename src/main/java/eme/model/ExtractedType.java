package eme.model;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.core.IType;

import eme.model.datatypes.ExtractedAttribute;

/**
 * Represents a type in the intermediate model.
 * @author Timur Saglam
 */
public abstract class ExtractedType extends ExtractedElement {
    protected final List<ExtractedAttribute> attributes;
    protected boolean innerType;
    protected IType iType;
    protected final List<ExtractedMethod> methods;
    protected String outerType;
    protected String superClass;
    protected final List<String> superInterfaces;

    /**
     * Basic constructor.
     * @param fullName is the full name, containing name and package name.
     */
    public ExtractedType(String fullName, IType iType) {
        super(fullName);
        this.iType = iType;
        superInterfaces = new LinkedList<String>();
        methods = new LinkedList<ExtractedMethod>();
        attributes = new LinkedList<ExtractedAttribute>();
        if (name.contains("$")) { // dollar in name means its a nested type
            innerType = true; // set nested true and adapt parent
            outerType = name.substring(0, name.lastIndexOf('$'));
        }
    }

    /**
     * Adds an attribute to the type.
     * @param attribute is the new attribute.
     */
    public void addAttribute(ExtractedAttribute attribute) {
        attributes.add(attribute);
    }

    /**
     * Adds an interface as super interface.
     * @param superInterface is the new super interface.
     */
    public void addInterface(String superInterface) {
        superInterfaces.add(superInterface);
    }

    /**
     * Adds a method to the type.
     * @param method is the new method.
     */
    public void addMethod(ExtractedMethod method) {
        methods.add(method);
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
     * Getter for the list of attributes.
     * @return the list of attributes.
     */
    public List<ExtractedAttribute> getAttributes() {
        return attributes;
    }

    /**
     * Getter for the JDT representation.
     * @return the JDT representation, an iType.
     */
    public IType getJDTRepresentation() {
        return iType;
    }

    /**
     * Getter for the list of methods.
     * @return the list of methods.
     */
    public List<ExtractedMethod> getMethods() {
        return methods;
    }

    /**
     * Returns the name of the outer type of an type, if that type is an inner type.
     * @return name of the outer type, null if the type is not an inner type.
     */
    public String getOuterType() {
        return outerType;
    }

    /**
     * Getter for the list of super interface names.
     * @return the list of super interface names.
     */
    public List<String> getSuperInterfaces() {
        return superInterfaces;
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