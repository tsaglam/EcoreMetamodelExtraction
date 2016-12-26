package eme.model;

/**
 * Represents an extracted variable in the model. Can be a method parameter or an class variable.
 * @author Timur Saglam
 */
public class ExtractedVariable {
    private final String name;
    private final String type;

    /**
     * Basic constructor, creates variable.
     * @param name is the name of the variable.
     * @param type is the type of the variable.
     */
    public ExtractedVariable(String name, String type) {
        this.name = name;
        this.type = type;
    }

    /**
     * Getter for the name.
     * @return the name.
     */
    public String getName() {
        return name;
    }

    /**
     * getter for the type.
     * @return the type.
     */
    public String getType() {
        return type;
    }
}