package eme.model;

import eme.model.datatypes.ExtractedDataType;

/**
 * Represents a class in the {@link IntermediateModel}.
 * @author Timur Saglam
 */
public class ExtractedClass extends ExtractedType {
    private final boolean abstractClass;
    private final boolean throwable;

    /**
     * Basic constructor.
     * @param fullName is the full name, containing name and package name.
     * @param abstractClass determines whether the class is abstract or not.
     * @param throwable determines whether the class inherits from {@link java.lang.Throwable}
     */
    public ExtractedClass(String fullName, boolean abstractClass, boolean throwable) {
        super(fullName);
        this.abstractClass = abstractClass;
        this.throwable = throwable;
    }

    /**
     * accessor for the super class.
     * @return the super class.
     */
    public ExtractedDataType getSuperClass() {
        return superClass;
    }

    /**
     * Checks whether the class is abstract.
     * @return true if class is abstract.
     */
    public boolean isAbstract() {
        return abstractClass;
    }

    /**
     * Checks whether the class is throwable.
     * @return true if class is throwable.
     */
    public boolean isThrowable() {
        return throwable;
    }

    /**
     * Sets a class as super class.
     * @param superClass is the new super class.
     */
    public void setSuperClass(ExtractedDataType superClass) {
        this.superClass = superClass;
    }
}
