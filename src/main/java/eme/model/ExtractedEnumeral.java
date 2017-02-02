package eme.model;

/**
 * Represents an enumeral in the {@link IntermediateModel}.
 * @author Timur Saglam
 */
public class ExtractedEnumeral {
    private final String name;

    /**
     * Basic constructor. Sets the name.
     * @param name is the name of the enumeral.
     */
    public ExtractedEnumeral(String name) {
        this.name = name;
    }

    /**
     * Accessor for the name of the enumeral.
     * @return the name.
     */
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}