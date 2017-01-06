package eme.model;

import org.eclipse.jdt.core.IType;

/**
 * Represents an interface in the intermediate model.
 * @author Timur Saglam
 */
public class ExtractedInterface extends ExtractedType {

    /**
     * Basic constructor.
     * @param fullName is the full name, containing name and package name.
     */
    public ExtractedInterface(String fullName) {
        super(fullName, null);
    }

    /**
     * Constructor that also takes the JDT representation.
     * @param fullName is the full name, containing name and package name.
     * @param iType is the JDT representation.
     */
    public ExtractedInterface(String fullName, IType iType) {
        super(fullName, iType);
    }
}
