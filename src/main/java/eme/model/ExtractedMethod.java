package eme.model;

import java.util.LinkedList;
import java.util.List;

import eme.model.datatypes.AccessLevelModifier;
import eme.model.datatypes.ExtractedDataType;
import eme.model.datatypes.ExtractedParameter;
import eme.model.datatypes.ExtractedTypeParameter;

/**
 * Represents a method of a {@link ExtractedType} in the {@link IntermediateModel}.
 * @author Timur Saglam
 */
public class ExtractedMethod extends ExtractedElement {
    private final List<ExtractedDataType> exceptions;
    private boolean isAbstract;
    private boolean isStatic;
    private MethodType methodType;
    private AccessLevelModifier modifier;
    private final List<ExtractedParameter> parameters;
    private final ExtractedDataType returnType;
    private List<ExtractedTypeParameter> typeParameters;

    /**
     * Basic constructor. Sets access level modifier to {@link AccessLevelModifier}.NO_MODIFIER, static and abstract to
     * false;
     * @param fullName is the full name of the method, consisting out of the full class name and the method name.
     * @param returnType is the data type for the return type of the method. null if it is void.
     */
    public ExtractedMethod(String fullName, ExtractedDataType returnType) {
        super(fullName);
        this.returnType = returnType;
        parameters = new LinkedList<ExtractedParameter>();
        exceptions = new LinkedList<ExtractedDataType>();
        typeParameters = new LinkedList<ExtractedTypeParameter>();
        modifier = AccessLevelModifier.NO_MODIFIER;
        methodType = MethodType.NORMAL;
    }

    /**
     * Adds a {@link ExtractedParameter} to the method.
     * @param parameter is the new parameter.
     */
    public void addParameter(ExtractedParameter parameter) {
        parameters.add(parameter);
    }

    /**
     * Adds a throws declaration to the method.
     * @param exception is the throws declaration.
     */
    public void addThrowsDeclaration(ExtractedDataType exception) {
        exceptions.add(exception);
    }

    /**
     * Accessor for the method type ({@link MethodType}).
     * @return the method type.
     */
    public MethodType getMethodType() {
        return methodType;
    }

    /**
     * accessor for the access level modifier ({@link AccessLevelModifier}).
     * @return the access level modifier of the method.
     */
    public AccessLevelModifier getModifier() {
        return modifier;
    }

    /**
     * accessor for the {@link ExtractedParameter}s.
     * @return the parameters
     */
    public List<ExtractedParameter> getParameters() {
        return parameters;
    }

    /**
     * accessor for the return type;
     * @return the returnType
     */
    public ExtractedDataType getReturnType() {
        return returnType;
    }

    /**
     * accessor for the throws declarations.
     * @return the throws declarations
     */
    public List<ExtractedDataType> getThrowsDeclarations() {
        return exceptions;
    }

    /**
     * accessor for the list of generic type parameters ({@link ExtractedTypeParameter}).
     * @return the list of generic type parameters.
     */
    public List<ExtractedTypeParameter> getTypeParameters() {
        return typeParameters;
    }

    /**
     * Checks whether the method is abstract.
     * @return true if it is abstract.
     */
    public boolean isAbstract() {
        return isAbstract;
    }

    /**
     * Checks whether the method is static.
     * @return true if it is static.
     */
    public boolean isStatic() {
        return isStatic;
    }

    /**
     * Mutator for the property abstract.
     * @param isAbstract determines whether the method is abstract or not.
     */
    public void setAbstract(boolean isAbstract) {
        this.isAbstract = isAbstract;
    }

    /**
     * Mutator for the method type.
     * @param methodType is the {@link MethodType}.
     */
    public void setMethodType(MethodType methodType) {
        this.methodType = methodType;
    }

    /**
     * Mutator for the modifier.
     * @param modifier is the {@link AccessLevelModifier}.
     */
    public void setModifier(AccessLevelModifier modifier) {
        this.modifier = modifier;
    }

    /**
     * Mutator for the property abstract.
     * @param isStatic determines whether the method is static or not.
     */
    public void setStatic(boolean isStatic) {
        this.isStatic = isStatic;
    }

    /**
     * Sets the generic type parameters.
     * @param typeParameters is the list of {@link ExtractedTypeParameter}s.
     */
    public void setTypeParameters(List<ExtractedTypeParameter> typeParameters) {
        this.typeParameters = typeParameters;
    }

    @Override
    public String toString() {
        String result = modifier + " " + parent.toString() + "." + name + parameters.toString();
        if (returnType != null) {
            result += " : " + returnType.toString();
        }
        return result;
    }
}