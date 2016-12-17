package eme.model;

/**
 * Represents a type in the intermediate model.
 * @author Timur Saglam
 */
public class ExtractedType extends ExtractedElement {
    protected boolean nested;
    protected String nestedIn;

    /**
     * Basic constructor.
     * @param fullName is the full name, containing name and package name.
     */
    public ExtractedType(String fullName) {
        super(fullName);
        if (name.contains("$")) { // dollar in name means its a nested type
            nested = true; // set nested true and adapt parent
            nestedIn = name.substring(0, name.lastIndexOf('$'));
        }
    }

    /**
     * Method checks whether the type is a nested type.
     * @return true if it is a nested type.
     */
    public boolean isNested() {
        return nested;
    }

    public static void main(String[] args) {
        ExtractedClass myClass = new ExtractedClass("main.sub.MyClass$NestedClass", false);
        System.out.println(myClass.isNested());
        System.out.println(myClass.getName());
        System.out.println(myClass.getParentName());
        System.out.println(myClass.getFullName());
    }
}
