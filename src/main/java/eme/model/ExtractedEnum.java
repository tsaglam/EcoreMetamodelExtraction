package eme.model;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents an enumeration in the {@link IntermediateModel}.
 * @author Timur Saglam
 */
public class ExtractedEnum extends ExtractedType {
    private final List<ExtractedEnumConstant> constants;

    /**
     * Basic constructor.
     * @param fullName is the full name, containing name and package name.
     */
    public ExtractedEnum(String fullName) {
        super(fullName);
        constants = new LinkedList<ExtractedEnumConstant>();
    }

    /**
     * Adds a enumeral to the enum
     * @param constant is the new value.
     */
    public void addConstant(ExtractedEnumConstant constant) {
        constants.add(constant);
    }

    /**
     * accessor for the enumerals of the enumeration.
     * @return the enumerals in a List.
     */
    public List<ExtractedEnumConstant> getConstants() {
        return constants;
    }
}