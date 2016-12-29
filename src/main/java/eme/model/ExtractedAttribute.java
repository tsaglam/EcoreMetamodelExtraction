package eme.model;

/**
 * Represents an extracted Attribute in the model
 * @author Timur Saglam
 */
public class ExtractedAttribute extends ExtractedVariable {

    private boolean finalAttribute;
    private AccessLevelModifier modifier;
    private boolean staticAttribute;

    /**
     * Basic constructor, creates the attribute.
     * @param identifier is the name of the attribute.
     * @param simpleName is the simple name of the type of the attribute, like "String", "List&ltint&gt" and "char[][]".
     * @param fullName is the full name of type of the attribute, like "java.lang.String", "java.util.list" and "char".
     */
    public ExtractedAttribute(String identifier, String simpleName, String fullName) {
        super(identifier, simpleName, fullName);
    }

    /**
     * Getter for the access level modifier.
     * @return the access level modifier of the method.
     */
    public AccessLevelModifier getModifier() {
        return modifier;
    }

    /**
     * Checks whether the attribute is final.
     * @return true if it is final.
     */
    public boolean isFinal() {
        return finalAttribute;
    }

    /**
     * Checks whether the method is static.
     * @return true if it is static.
     */
    public boolean isStatic() {
        return staticAttribute;
    }

    /**
     * Sets the flags of the attribute. That means whether the attribute is static, whether the attribute is final and
     * what access level modifier it has.
     * @param modifier is the access level modifier.
     * @param staticAttribute determines whether the attribute is static or not.
     * @param finalAttribute determines whether the attribute is final or not.
     */
    public void setFlags(AccessLevelModifier modifier, boolean staticMethod, boolean finalAttribute) {
        this.modifier = modifier;
        this.staticAttribute = staticMethod;
        this.finalAttribute = finalAttribute;
    }

    @Override
    public String toString() {
        return getClass() + "(" + getIdentifier() + ", " + getTypeName() + ", " + getFullTypeName() + ")";
    }
}
