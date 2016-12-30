package eme.model;

/**
 * Represents an extracted variable in the model. Can be a method parameter or an class variable.
 * @author Timur Saglam
 */
public class ExtractedVariable extends ExtractedDataType {
    private final String identifier;

    /**
     * Basic constructor, creates variable.
     * @param identifier is the name of the variable.
     * @param fullName is the full name of type of the variable, like "java.lang.String", "java.util.list" and "char".
     */
    public ExtractedVariable(String identifier, String fullName, int arrayCount) {
        super(fullName, arrayCount);
        this.identifier = identifier;
    }

    /**
     * Getter for the identifier of the variable.
     * @return the identifier.
     */
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + getFullTypeName() + " " + identifier + ")";
    }
}