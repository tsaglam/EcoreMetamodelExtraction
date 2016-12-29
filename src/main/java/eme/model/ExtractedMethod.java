package eme.model;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents a method of a class in the model.
 * @author Timur Saglam
 */
public class ExtractedMethod extends ExtractedElement {
    private boolean abstractMethod;
    private final boolean constructor;
    private AccessLevelModifier modifier;
    private final List<ExtractedVariable> parameters;
    private final ExtractedDataType returnType;
    private boolean staticMethod;

    /**
     * Basic constructor.
     * @param fullName is the full name of the method, consisting out of the full class name and the method name.
     */
    public ExtractedMethod(String fullName, ExtractedDataType returnType, boolean constructor) {
        super(fullName);
        parameters = new LinkedList<ExtractedVariable>();
        this.returnType = returnType;
        this.constructor = constructor;
    }

    /**
     * Adds a parameter to the method.
     * @param parameter is the new parameter.
     */
    public void addParameter(ExtractedVariable parameter) {
        parameters.add(parameter);
    }

    /**
     * Getter for the access level modifier.
     * @return the access level modifier of the method.
     */
    public AccessLevelModifier getModifier() {
        return modifier;
    }

    /**
     * getter for the parameters.
     * @return the parameters
     */
    public List<ExtractedVariable> getParameters() {
        return parameters;
    }

    /**
     * getter for the return type;
     * @return the returnType
     */
    public ExtractedDataType getReturnType() {
        return returnType;
    }

    /**
     * Checks whether the method is abstract.
     * @return true if it is abstract.
     */
    public boolean isAbstract() {
        return abstractMethod;
    }

    /**
     * Checks whether the method is a constructor.
     * @return true if it is a constructor.
     */
    public boolean isConstructor() {
        return constructor;
    }

    /**
     * Checks whether the method is static.
     * @return true if it is static.
     */
    public boolean isStatic() {
        return staticMethod;
    }

    /**
     * Sets the flags of the method. That means whether the method is static, whether the method is abstract and what
     * access level modifier it has.
     * @param modifier is the access level modifier.
     * @param staticMethod determines whether the method is static or not.
     * @param abstractMethod determines whether the method is abstract or not.
     */
    public void setFlags(AccessLevelModifier modifier, boolean staticMethod, boolean abstractMethod) {
        this.modifier = modifier;
        this.staticMethod = staticMethod;
        this.abstractMethod = abstractMethod;
    }
}