package eme.model;

/**
 * Represents a type in the intermediate model.
 * @author Timur Saglam
 */
public class ExtractedType extends ExtractedElement {
    private String name;
    private String packageName;

    /**
     * Basic constructor.
     * @param fullName is the full name, containing name and package name.
     */
    public ExtractedType(String fullName) {
        name = createName(fullName);
        packageName = createPath(fullName);
    }

    /**
     * Getter for the name of the type.
     * @return the name of the type.
     */
    public String getName() {
        return name;
    }

    /**
     * Getter for the package of the type.
     * @return the package path of the type
     */
    public String getPackageName() {
        return packageName;
    }

    @Override
    public String toString() {
        return name;
    }

}
