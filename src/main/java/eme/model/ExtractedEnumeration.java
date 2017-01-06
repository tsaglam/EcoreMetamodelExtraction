package eme.model;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.core.IType;

/**
 * Represents an enumeration in the intermediate model.
 * @author Timur Saglam
 */
public class ExtractedEnumeration extends ExtractedType {

    private final List<String> enumerals;

    /**
     * Basic constructor.
     * @param fullName is the full name, containing name and package name.
     */
    public ExtractedEnumeration(String fullName) {
        super(fullName, null);
        enumerals = new LinkedList<String>();
    }

    /**
     * Constructor that also takes the JDT representation.
     * @param fullName is the full name, containing name and package name.
     * @param iType is the JDT representation.
     */
    public ExtractedEnumeration(String fullName, IType iType) {
        super(fullName, iType);
        enumerals = new LinkedList<String>();
    }

    /**
     * Adds a enumeral to the enum
     * @param enumeral is the new value.
     */
    public void addEnumeral(String enumeral) {
        enumerals.add(enumeral);
    }

    /**
     * Getter for the enumerals of the enumeration.
     * @return the enumerals in a List.
     */
    public List<String> getEnumerals() {
        return enumerals;
    }

}
