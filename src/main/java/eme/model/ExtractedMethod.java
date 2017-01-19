package eme.model;

import java.util.LinkedList;
import java.util.List;

import eme.model.datatypes.AccessLevelModifier;
import eme.model.datatypes.ExtractedDataType;
import eme.model.datatypes.ExtractedParameter;

/**
 * Represents a method of a class in the model.
 * @author Timur Saglam
 */
public class ExtractedMethod extends ExtractedElement {
    private boolean accessor;
    private final boolean constructor;
    private final List<ExtractedDataType> exceptions;
    private boolean isAbstract;
    private boolean isStatic;
    private AccessLevelModifier modifier;
    private boolean mutator;
    private final List<ExtractedParameter> parameters;
    private final ExtractedDataType returnType;

    /**
     * Basic constructor. Sets access level modifier to NO_MODIFIER, static and abstract to false;
     * @param fullName is the full name of the method, consisting out of the full class name and the method name.
     * @param returnType is the data type for the return type of the method. null if it is void.
     * @param constructor determines whether this method is an constructor.
     */
    public ExtractedMethod(String fullName, ExtractedDataType returnType, boolean constructor) {
        super(fullName);
        parameters = new LinkedList<ExtractedParameter>();
        exceptions = new LinkedList<ExtractedDataType>();
        this.returnType = returnType;
        this.constructor = constructor;
        modifier = AccessLevelModifier.NO_MODIFIER;
    }

    /**
     * Adds a parameter to the method.
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
     * accessor for the access level modifier.
     * @return the access level modifier of the method.
     */
    public AccessLevelModifier getModifier() {
        return modifier;
    }

    /**
     * accessor for the parameters.
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
     * Checks whether the method is abstract.
     * @return true if it is abstract.
     */
    public boolean isAbstract() {
        return isAbstract;
    }

    /**
     * Checks whether the method is a accessor method.
     * @return true if it is a accessor method.
     */
    public boolean isAccessor() {
        return accessor;
    }

    /**
     * Checks whether the method is a constructor.
     * @return true if it is a constructor.
     */
    public boolean isConstructor() {
        return constructor;
    }

    /**
     * Checks whether the method is a mutator method.
     * @return true if it is a mutator method.
     */
    public boolean isMutator() {
        return mutator;
    }

    /**
     * Checks whether the method is static.
     * @return true if it is static.
     */
    public boolean isStatic() {
        return isStatic;
    }

    /**
     * Sets the flags of the method. That means whether the method is static, whether the method is abstract and what
     * access level modifier it has.
     * @param modifier is the access level modifier.
     * @param isStatic determines whether the method is static or not.
     * @param isAbstract determines whether the method is abstract or not.
     */
    public void setFlags(AccessLevelModifier modifier, boolean isStatic, boolean isAbstract, boolean accessor, boolean mutator) {
        this.modifier = modifier;
        this.isStatic = isStatic;
        this.isAbstract = isAbstract;
        this.accessor = accessor;
        this.mutator = mutator;
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