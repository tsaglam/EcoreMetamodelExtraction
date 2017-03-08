package eme.model;

import java.util.LinkedList;
import java.util.List;

import eme.model.datatypes.ExtractedField;
import eme.model.datatypes.ExtractedDataType;
import eme.model.datatypes.ExtractedTypeParameter;

/**
 * Represents a type in the {@link IntermediateModel}.
 * @author Timur Saglam
 */
public abstract class ExtractedType extends ExtractedElement {
    protected final List<ExtractedField> attributes;
    protected final List<ExtractedMethod> methods;
    protected String outerType;
    protected ExtractedDataType superClass;
    protected final List<ExtractedDataType> superInterfaces;
    protected final List<ExtractedTypeParameter> typeParameters;

    /**
     * Basic constructor.
     * @param fullName is the full name, containing name and package name.
     */
    public ExtractedType(String fullName) {
        super(fullName);
        superInterfaces = new LinkedList<ExtractedDataType>();
        methods = new LinkedList<ExtractedMethod>();
        attributes = new LinkedList<ExtractedField>();
        typeParameters = new LinkedList<ExtractedTypeParameter>();
    }

    /**
     * Adds an {@link ExtractedField} to the type.
     * @param attribute is the new {@link ExtractedField}.
     */
    public void addAttribute(ExtractedField attribute) {
        attributes.add(attribute);
    }

    /**
     * Adds an interface as super interface.
     * @param superInterface is the new super interface.
     */
    public void addInterface(ExtractedDataType superInterface) {
        superInterfaces.add(superInterface);
    }

    /**
     * Adds a {@link ExtractedMethod} to the type.
     * @param method is the new {@link ExtractedMethod}.
     */
    public void addMethod(ExtractedMethod method) {
        methods.add(method);
    }

    /**
     * Adds a generic type parameter ({@link ExtractedTypeParameter}) to the type.
     * @param typeParameter is the new {@link ExtractedTypeParameter}.
     */
    public void addTypeParameter(ExtractedTypeParameter typeParameter) {
        typeParameters.add(typeParameter);
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
     * accessor for the list of {@link ExtractedField}s.
     * @return the list of attributes.
     */
    public List<ExtractedField> getAttributes() {
        return attributes;
    }

    /**
     * accessor for the list of {@link ExtractedMethod}s.
     * @return the list of methods.
     */
    public List<ExtractedMethod> getMethods() {
        return methods;
    }

    /**
     * Accessor for the name of the types outer type.
     * @return the outer type name or null if it is not a inner type.
     */
    public String getOuterType() {
        return outerType;
    }

    /**
     * accessor for the list of super interfaces.
     * @return the list of super interfaces.
     */
    public List<ExtractedDataType> getSuperInterfaces() {
        return superInterfaces;
    }

    /**
     * accessor for the list of generic type parameters ({@link ExtractedTypeParameter}).
     * @return the list of generic type parameters.
     */
    public List<ExtractedTypeParameter> getTypeParameters() {
        return typeParameters;
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
        return outerType != null;
    }

    /**
     * Mutator for the name of the types outer type.
     * @param outerType is the name.
     */
    public void setOuterType(String outerType) {
        this.outerType = outerType;
    }
}