package eme.model;

/**
 * This enum helps to differ between the different method types.
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
     * For normal methods.
     */
    METHOD,

    /**
     * For mutator methods.
     */
    MUTATOR;
}
