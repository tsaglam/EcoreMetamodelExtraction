package eme.properties;

/**
 * Enumeration for the extraction property keys and their default values.
 * @author Timur Saglam
 */
public enum ExtractionProperty {
    ABSTRACT_METHODS("ExtractAbstractMethods", "true"),
    ACCESS_METHODS("ExtractAccessMethods", "false"),
    CONSTRUCTORS("ExtractConstructors", "false"),
    DATATYPE_PACKAGE("DataTypePackageName", "DATATYPES"),
    DEFAULT_ATTRIBUTES("ExtractDefaultAttributes", "true"),
    DEFAULT_METHODS("ExtractDefaultMethods", "true"),
    DEFAULT_PACKAGE("DefaultPackageName", "DEFAULT"),
    EMPTY_PACKAGES("ExtractEmptyPackages", "true"),
    NESTED_TYPES("ExtractNestedTypes", "false"),
    PRIVATE_ATTRIBUTES("ExtractPrivateAttributes", "false"),
    PRIVATE_METHODS("ExtractPrivateMethods", "false"),
    PROTECTED_ATTRIBUTES("ExtractProtectedAttributes", "false"),
    PROTECTED_METHODS("ExtractProtectedMethods", "false"),
    PUBLIC_ATTRIBUTES("ExtractPublicAttributes", "true"),
    SAVING_STRATEGY("SavingStrategy", "NewProject"),
    STATIC_ATTRIBUTES("ExtractStaticAttributes", "false"),
    STATIC_METHODS("ExtractStaticMethods", "false"),
    THROWABLES("ExtractThrowables", "false");

    private final boolean binary;
    private final String defaultValue;
    private final String key;

    /**
     * Private constructor for enum values with key and default value of an extraction property.
     */
    ExtractionProperty(String key, String defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
        binary = "true".equals(defaultValue) || "false".equals(defaultValue);
    }

    /**
     * Accessor for the default value String.
     * @return the default value.
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * Accessor for the key String.
     * @return the key.
     */
    public String getKey() {
        return key;
    }

    /**
     * Checks whether a extraction property can be interpreted as boolean.
     * @return the true if it can.
     */
    public boolean isBinary() {
        return binary;
    }
}