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

    /**
     * Basic constructor.
     * @param fullName is the full name of the method, consisting out of the full class name and the
     * method name.
     */
    public ExtractedMethod(String fullName) {
        super(fullName);
        parameters = new LinkedList<ExtractedVariable>();
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
}
