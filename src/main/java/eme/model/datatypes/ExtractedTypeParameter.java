package eme.model.datatypes;

import java.util.LinkedList;
import java.util.List;

/**
 * This class represents a generic type parameter of a Java class.
 * @author Timur Saglam
 */
public class ExtractedTypeParameter {
    private final List<ExtractedDataType> bounds;
    private final String identifier;

    /**
     * Basic constructor for type parameter.
     * @param identifier is the identifier, which normally is an upper case letter.
     */
    public ExtractedTypeParameter(String identifier) {
        this.identifier = identifier;
        bounds = new LinkedList<ExtractedDataType>();
    }

    /**
     * Adds an bound to the type parameters bounds.
     * @param bound is the new bound.
     */
    public void add(ExtractedDataType bound) {
        bounds.add(bound);
    }

    /**
     * accessor for the bounds.
     * @return the bounds of the type parameter.
     */
    public List<ExtractedDataType> getBounds() {
        return bounds;
    }

    /**
     * accessor for the identifier.
     * @return the identifier of the type parameter.
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Checks if the type parameter has bounds or not.
     * @return true if it has at least one bound.
     */
    public boolean hasBounds() {
        return !bounds.isEmpty();
    }

    @Override
    public String toString() {
        if (hasBounds()) {
            return identifier + " extends " + bounds.toString();
        }
        return identifier;
    }
}
