package eme.properties;

/**
 * Enumeration for the extraction properties that are interpreted as Strings.
 * @author Timur Saglam
 */
public enum TextProperty {
    DATATYPE_PACKAGE("DataTypePackageName", "DATATYPES"),
    DEFAULT_PACKAGE("DefaultPackageName", "DEFAULT"),
    SAVING_STRATEGY("SavingStrategy", "NewProject");

    private final String defaultValue;
    private final String key;

    /**
     * Private constructor for enum values with key and default value of an extraction property.
     */
    TextProperty(String key, String defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
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
}