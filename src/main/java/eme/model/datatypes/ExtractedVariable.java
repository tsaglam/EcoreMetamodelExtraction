package eme.model.datatypes;

import eme.model.IntermediateModel;

/**
 * Represents an extracted variable in {@link IntermediateModel}
 * @author Timur Saglam
 */
public class ExtractedVariable extends ExtractedDataType {
    private final String identifier;

    /**
     * Basic constructor, creates parameter.
     * @param identifier is the name of the parameter.
     * @param fullTypeName is the full name of type of the parameter, like "java.lang.String", "java.util.list" and
     * "char".
     * @param arrayDimension is the amount of array dimensions, should be 0 if it is not an array.
     */
    public ExtractedVariable(String identifier, String fullTypeName, int arrayDimension) {
        super(fullTypeName, arrayDimension);
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
        return getClass().getSimpleName() + "(" + typeString() + " " + identifier + ")";
    }
}