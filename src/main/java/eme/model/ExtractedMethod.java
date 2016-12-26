package eme.model;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents a method of a class in the model.
 * @author Timur Saglam
 */
public class ExtractedMethod extends ExtractedElement {
    private final List<ExtractedVariable> parameters;
    private String returnType;
    private boolean staticMethod;

    /**
     * Basic constructor.
     * @param fullName is the full name of the method, consisting out of the full class name and the
     * method name.
     */
    public ExtractedMethod(String fullName, String returnType, boolean staticMethod) {
        super(fullName);
        parameters = new LinkedList<ExtractedVariable>();
        this.returnType = returnType;
        this.staticMethod = staticMethod;
    }

    /**
     * Adds a parameter to the method.
     * @param parameter is the new parameter.
     */
    public void addParameter(ExtractedVariable parameter) {
        parameters.add(parameter);
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
    public String getReturnType() {
        return returnType;
    }

    /**
     * Checks whether the method is static.
     * @return true if it is static.
     */
    public boolean isStatic() {
        return staticMethod;
    }
}