package eme.model.datatypes;

/**
 * Represents the wild card status of an {@link ExtractedDataType}.
 */
public enum WildcardStatus {
    /**
     * Is not a wild card data type.
     */
    NO_WILDCARD,

    /**
     * Is a basic wild card data type like <code>?</code>
     */
    UNBOUND,

    /**
     * Is a wild card data type with lower bound like <code>? super SomeClass/<code>
     */
    LOWER_BOUND,

    /**
     * Is a wild card data type with upper bound like <code>? extends SomeClass/<code>
     */
    UPPER_BOUND;
}