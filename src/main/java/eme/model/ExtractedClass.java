package eme.model;

/**
 * Represents a class in the intermediate model.
 * @author Timur Saglam
 */
public class ExtractedClass extends ExtractedElement {
    private String name;
    private String packageName;

    /**
     * Basic constructor.
     */
    public ExtractedClass(String fullName) {
        name = createName(fullName);
        packageName = createPath(fullName);
    }

    /**
     * Getter for the name of the class.
     * @return the name of the class.
     */
    public String getName() {
        return name;
    }

    /**
     * Getter for the package of the class.
     * @return the package path of the class
     */
    public String getPackageName() {
        return packageName;
    }

    @Override
    public String toString() {
        return name;
    }

}
