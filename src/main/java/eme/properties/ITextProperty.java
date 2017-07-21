package eme.properties;

/**
 * Interface for text properties used with an instance of {@link AbstractProperties}.
 * @author Timur Saglam
 */
public interface ITextProperty {
    /**
     * Accessor for the default value String.
     * @return the default value.
     */
    String getDefaultValue();

    /**
     * Accessor for the key String.
     * @return the key.
     */
    String getKey();
}