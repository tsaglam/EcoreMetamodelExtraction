package eme.model;

/**
 * Represents a data type in the model.
 * @author Timur Saglam
 */
public class ExtractedDataType {
    private final String simpleName;
    private final String fullName;
    private int arrayDimension;
    private int genericArguments;

    /**
     * Basic constructor, takes the full and the simple name.
     * @param simpleName is the simple name of the data type, like "String", "List&ltint&gt" and
     * "char[][]".
     * @param fullName is the full name of the data type, like "java.lang.String", "java.util.list"
     * and "char".
     */
    public ExtractedDataType(String simpleName, String fullName) {
        this.simpleName = simpleName;
        this.fullName = fullName;
        if (simpleName.contains("<")) {
            genericArguments = 1; // at least one
        }
        for (int i = 0; i < simpleName.length(); i++) {
            if (simpleName.charAt(i) == ',') { // TODO (HIGH) fix generic parsing
                genericArguments++; // count additional generic arguments
            }
        }
        arrayDimension = fullName.split("\\[").length - 1; // count array dimensions
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
     * getter for the number of generic arguments.
     * @return the the number of generic arguments, 0 if the type is not generic.
     */
    public int getGenericArguments() {
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
        return genericArguments > 0;
    }

    @Override
    public String toString() {
        return getClass() + "(" + simpleName + ", " + fullName + ")";
    }
}
