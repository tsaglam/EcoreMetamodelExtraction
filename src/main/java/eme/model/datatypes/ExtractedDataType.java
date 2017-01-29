package eme.model.datatypes;

import java.util.LinkedList;
import java.util.List;

import eme.model.IntermediateModel;

/**
 * Represents a data type in the {@link IntermediateModel}.
 * @author Timur Saglam
 */
public class ExtractedDataType {
    private final int arrayDimension;
    private String fullTypeName;
    private List<ExtractedDataType> genericArguments;
    private String typeName;
    private WildcardStatus wildcardStatus;

    /**
     * Basic constructor, takes the full and the simple name.
     * @param fullName is the full name of the data type, like "java.lang.String", "java.util.list" and "char".
     * @param arrayDimension is the amount of array dimensions, should be 0 if it is not an array.
     */
    public ExtractedDataType(String fullName, int arrayDimension) {
        this.fullTypeName = fullName;
        this.arrayDimension = arrayDimension;
        genericArguments = new LinkedList<ExtractedDataType>();
        wildcardStatus = WildcardStatus.NO_WILDCARD;
        buildNames(); // build full and simple name
    }

    /**
     * accessor for the array dimension.
     * @return the array dimension, 0 if the type is not an array.
     */
    public int getArrayDimension() {
        return arrayDimension;
    }

    /**
     * accessor for the full type name, which includes the packages and the array brackets.
     * @return the full type name.
     */
    public String getFullType() {
        return fullTypeName;
    }

    /**
     * accessor for the generic arguments.
     * @return the List of generic arguments of this data type.
     */
    public List<ExtractedDataType> getGenericArguments() {
        return genericArguments;
    }

    /**
     * accessor for the simple type name, which is the basic name.
     * @return the simple type name or, if it is an array type, the array type name.
     */
    public String getType() {
        return typeName;
    }

    /**
     * accessor for the wild card status.
     * @return the wild card status.
     */
    public WildcardStatus getWildcardStatus() {
        return wildcardStatus;
    }

    /**
     * Checks whether the data type is an array.
     * @return true if it is an array.
     */
    public boolean isArray() {
        return arrayDimension > 0;
    }

    /**
     * Checks whether the data type is a generic type.
     * @return true if it is generic.
     */
    public boolean isGeneric() {
        return !genericArguments.isEmpty();
    }

    /**
     * Checks whether the data type is an wild card.
     * @return true if it is an wild card.
     */
    public boolean isWildcard() {
        return wildcardStatus != WildcardStatus.NO_WILDCARD;
    }

    /**
     * mutator for the generic arguments.
     * @param genericArguments is the list of generic arguments.
     */
    public void setGenericArguments(List<ExtractedDataType> genericArguments) {
        this.genericArguments = genericArguments;
    }

    /**
     * Sets the wild card status of the data type.
     * @param status is the status to set.
     */
    public void setWildcardStatus(WildcardStatus status) {
        wildcardStatus = status;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + fullTypeName + ")";
    }

    /**
     * Builds the full and simple name from the initial full name. The full name has to be set.
     */
    private final void buildNames() {
        typeName = fullTypeName.contains(".") ? fullTypeName.substring(fullTypeName.lastIndexOf('.') + 1) : fullTypeName;
        typeName = isArray() ? typeName + "Array" : typeName; // add "Array" if is array
        typeName = arrayDimension > 1 ? typeName + arrayDimension + "D" : typeName; // add dimension
        for (int i = 0; i < arrayDimension; i++) { // adjust array names to dimension
            this.fullTypeName += "[]";
        }
    }
}