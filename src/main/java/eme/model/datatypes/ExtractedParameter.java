package eme.model.datatypes;

import eme.model.IntermediateModel;

/**
 * Represents an extracted parameter in the {@link IntermediateModel}.
 * @author Timur Saglam
 */
public class ExtractedParameter extends ExtractedVariable {

    /**
     * Basic constructor, creates parameter.
     * @param identifier is the name of the parameter.
     * @param fullTypeName is the full name of type of the parameter, like "java.lang.String", "java.util.list" and
     * "char".
     * @param arrayDimension is the amount of array dimensions, should be 0 if it is not an array.
     */
    public ExtractedParameter(String identifier, String fullTypeName, int arrayDimension) {
        super(identifier, fullTypeName, arrayDimension);
    }
}