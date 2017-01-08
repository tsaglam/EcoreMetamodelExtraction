package eme.model.datatypes;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents a data type in the model.
 * @author Timur Saglam
 */
public class ExtractedDataType {
    private final int arrayDimension;
    private final String fullName;
    private List<ExtractedDataType> genericArguments;
    private final String simpleName;

    /**
     * Basic constructor, takes the full and the simple name.
     * @param fullName is the full name of the data type, like "java.lang.String", "java.util.list" and "char".
     * @param arrayCount is the amount of array dimensions, should be 0 if it is not an array.
     */
    public ExtractedDataType(String fullName, int arrayCount) {
        this.fullName = fullName;
        simpleName = fullName.contains(".") ? fullName.substring(fullName.lastIndexOf(".") + 1) : fullName;
        genericArguments = new LinkedList<ExtractedDataType>();
        arrayDimension = arrayCount;
    }

    /**
     * Getter for the array dimension.
     * @return the array dimension, 0 if the type is not an array.
     */
    public int getArrayDimension() {
        return arrayDimension;
    }

    /**
     * getter for the full type name.
     * @return the full type name.
     */
    public String getFullTypeName() {
        return fullName;
    }

    /**
     * getter for the generic arguments.
     * @return the List of generic arguments of this data type.
     */
    public List<ExtractedDataType> getGenericArguments() {
        return genericArguments;
    }

    /**
     * getter for the simple type name.
     * @return the simple type name.
     */
    public String getTypeName() {
        return simpleName;
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
        return genericArguments.size() > 0;
    }

    /**
     * Setter for the generic arguments.
     * @param genericArguments is the list of generic arguments.
     */
    public void setGenericArguments(List<ExtractedDataType> genericArguments) {
        this.genericArguments = genericArguments;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + fullName + ")";
    }
}