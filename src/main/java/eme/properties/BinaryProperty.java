package eme.properties;

/**
 * Enumeration for the extraction properties that can be interpreted as boolean values.
 * @author Timur Saglam
 */
public enum BinaryProperty implements IBinaryProperty {
    ABSTRACT_METHODS("ExtractAbstractMethods", true),
    ACCESS_METHODS("ExtractAccessMethods", false),
    CLASSES("ExtractClasses", true),
    CONSTRUCTORS("ExtractConstructors", false),
    DEFAULT_FIELDS("ExtractDefaultFields", true),
    DEFAULT_METHODS("ExtractDefaultMethods", true),
    DUMMY_CLASS("GenerateDummyClass", false),
    ROOT_CONTAINER("GenerateRootContainer", true),
    EMPTY_PACKAGES("ExtractEmptyPackages", true),
    ENUMS("ExtractEnumerations", true),
    INTERFACES("ExtractInterfaces", true),
    NESTED_TYPES("ExtractNestedTypes", false),
    PRIVATE_FIELDS("ExtractPrivateFields", false),
    PRIVATE_METHODS("ExtractPrivateMethods", false),
    PROTECTED_FIELDS("ExtractProtectedFields", false),
    PROTECTED_METHODS("ExtractProtectedMethods", false),
    PUBLIC_FIELDS("ExtractPublicFields", true),
    PUBLIC_METHODS("ExtractPublicMethods", true),
    STATIC_FIELDS("ExtractStaticFields", false),
    STATIC_METHODS("ExtractStaticMethods", false),
    THROWABLES("ExtractThrowables", false),
    FINAL_AS_UNCHANGEABLE("FinalAsUnchangeable", true),
    FIELD_MULTIPLICITIES("FieldMultiplicities", true),
    PARAMETER_MULTIPLICITIES("ParameterMultiplicities", true),
    RETURN_TYPE_MULTIPLICITIES("ReturnTypeMultiplicities", true),
    CUSTOM_EXTRACTION_SCOPE("CustomExtractionScope", true);

    private final boolean defaultValue;
    private final String key;

    /**
     * Private constructor for enum values with key and default value of an extraction property.
     * @param key is the key of the property.
     * @param defaultValue is the default value of the property.
     */
    BinaryProperty(String key, boolean defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    }

    @Override
    public boolean getDefaultValue() {
        return defaultValue;
    }

    @Override
    public String getKey() {
        return key;
    }
}