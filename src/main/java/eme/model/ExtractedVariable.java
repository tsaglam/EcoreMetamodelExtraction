package eme.model;

/**
 * Represents an extracted variable in the model. Can be a method parameter or an class variable.
 * @author Timur Saglam
 */
public class ExtractedVariable extends ExtractedDataType {
    private final String identifier;

    /**
     * Basic constructor, creates variable.
     * @param name is the name of the variable.
     * @param simpleName is the simple name of the type of the variable, like "String",
     * "List&ltint&gt" and "char[][]".
     * @param fullName is the full name of type of the variable, like "java.lang.String",
     * "java.util.list" and "char".
     */
    public ExtractedVariable(String name, String simpleName, String fullName) {
        super(simpleName, fullName);
        this.identifier = name;
    }

    /**
     * Getter for the name of the variable.
     * @return the name.
     */
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String toString() {
        return super.toString() + " " + identifier;
    }
}