package eme.model.datatypes;

import java.util.LinkedList;
import java.util.List;

/**
 * This class represents a generic data type of a Java class.
 * @author Timur Saglam
 */
public class ExtractedGenericType {
    private List<ExtractedDataType> extendedTypes; // TODO can be multiple, should be data type
    private final String identifier;

    /**
     * Basic constructor for generic type.
     * @param identifier is the identifier, which normally is an upper case letter.
     */
    public ExtractedGenericType(String identifier) {
        this.identifier = identifier;
        extendedTypes = new LinkedList<ExtractedDataType>();
    }

    /**
     * Adds an extended type to the generic type.
     * @param extendedType is the new extended type.
     */
    public void add(ExtractedDataType extendedType) {
        extendedTypes.add(extendedType);
    }

    /**
     * Checks if the generic type extends a type or not.
     * @return if it extends a type.
     */
    public boolean extendsType() {
        return !extendedTypes.isEmpty();
    }

    /**
     * Getter for the extended types.
     * @return the extended types of the generic type.
     */
    public List<ExtractedDataType> getExtendedTypes() {
        return extendedTypes;
    }

    /**
     * Getter for the identifier.
     * @return the identifier of the generic type.
     */
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String toString() {
        if (extendsType()) {
            return identifier + " extends " + extendedTypes.toString();
        }
        return identifier;
    }
}
