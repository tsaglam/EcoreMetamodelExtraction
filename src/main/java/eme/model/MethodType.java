package eme.model;

/**
 * This enum helps to differ between the different method types of an {@link ExtractedMethod}.
 * @author Timur Saglam
 */
public enum MethodType {
    /**
     * For accessor methods.
     */
    ACCESSOR,

    /**
     * For constructors.
     */
    CONSTRUCTOR,

    /**
     * For main methods.
     */
    MAIN,

    /**
     * For mutator methods.
     */
    MUTATOR,

    /**
     * For normal methods.
     */
    NORMAL;

    @Override
    public String toString() {
        return super.toString().toLowerCase() + " method";
    }
}