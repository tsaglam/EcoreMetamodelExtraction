package eme.model.datatypes;

import eme.model.IntermediateModel;

/**
 * Represents an extracted parameter in the {@link IntermediateModel}.
 * @author Timur Saglam
 */
public class ExtractedParameter extends ExtractedDataType {
    private final String identifier;

    /**
     * Basic constructor, creates parameter.
     * @param identifier is the name of the parameter.
     * @param fullName is the full name of type of the parameter, like "java.lang.String", "java.util.list" and "char".
     * @param arrayCount is the amount of array dimensions, should be 0 if it is not an array.
     */
    public ExtractedParameter(String identifier, String fullName, int arrayCount) {
        super(fullName, arrayCount);
        this.identifier = identifier;
    }

    /**
     * accessor for the identifier of the parameter.
     * @return the identifier.
     */
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + getFullType() + " " + identifier + ")";
    }
}