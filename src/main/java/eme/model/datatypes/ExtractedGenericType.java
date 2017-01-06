package eme.model.datatypes;

/**
 * This class represents a generic data type of a Java class.
 * @author Timur Saglam
 */
public class ExtractedGenericType {
    private String extendedType;
    private final String identifier;

    /**
     * Basic constructor for generic type.
     * @param identifier is the identifier, which normally is an upper case letter.
     */
    public ExtractedGenericType(String identifier) {
        this.identifier = identifier;
    }

    /**
     * Checks if the generic type extends a type or not.
     * @return if it extends a type.
     */
    public boolean extendsType() {
        return extendedType != null;
    }

    /**
     * Getter for the extended type.
     * @return the extended type of the generic type, null if it has no type.
     */
    public String getExtendedType() {
        return extendedType;
    }

    /**
     * Getter for the identifier.
     * @return the identifier of the generic type.
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Setter for the extended type. Adds an extended type to the generic type.
     * @param extendedType is the new extended type.
     */
    public void setExtendedType(String extendedType) {
        this.extendedType = extendedType;
    }

    @Override
    public String toString() {
        if (extendsType()) {
            return identifier + " extends " + extendedType;
        }
        return identifier;
    }
}
