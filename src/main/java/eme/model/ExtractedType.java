package eme.model;

/**
 * Represents a type in the intermediate model.
 * @author Timur Saglam
 */
public class ExtractedType extends ExtractedElement {

    /**
     * Basic constructor.
     * @param fullName is the full name, containing name and package name.
     */
    public ExtractedType(String fullName) {
        super(fullName);
    }
}
