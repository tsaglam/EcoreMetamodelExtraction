package eme.properties;

/**
 * Enumeration for the extraction properties that can be interpreted as boolean values.
 * @author Timur Saglam
 */
public enum BinaryProperty {
    ABSTRACT_METHODS("ExtractAbstractMethods", true),
    ACCESS_METHODS("ExtractAccessMethods", false),
    CONSTRUCTORS("ExtractConstructors", false),
    DEFAULT_ATTRIBUTES("ExtractDefaultAttributes", true),
    DEFAULT_METHODS("ExtractDefaultMethods", true),
    EMPTY_PACKAGES("ExtractEmptyPackages", true),
    NESTED_TYPES("ExtractNestedTypes", false),
    PRIVATE_ATTRIBUTES("ExtractPrivateAttributes", false),
    PRIVATE_METHODS("ExtractPrivateMethods", false),
    PROTECTED_ATTRIBUTES("ExtractProtectedAttributes", false),
    PROTECTED_METHODS("ExtractProtectedMethods", false),
    PUBLIC_ATTRIBUTES("ExtractPublicAttributes", true),
    STATIC_ATTRIBUTES("ExtractStaticAttributes", false),
    STATIC_METHODS("ExtractStaticMethods", false),
    THROWABLES("ExtractThrowables", false);

    private final boolean defaultValue;
    private final String key;

    /**
     * Private constructor for enum values with key and default value of an extraction property.
     */
    BinaryProperty(String key, boolean defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    }

    /**
     * Accessor for the default value.
     * @return the default value.
     */
    public boolean getDefaultValue() {
        return defaultValue;
    }

    /**
     * Accessor for the key String.
     * @return the key.
     */
    public String getKey() {
        return key;
    }
}