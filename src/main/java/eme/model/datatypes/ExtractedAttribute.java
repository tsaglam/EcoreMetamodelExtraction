package eme.model.datatypes;

/**
 * Represents an extracted Attribute in the model
 * @author Timur Saglam
 */
public class ExtractedAttribute extends ExtractedParameter {

    private boolean finalAttribute;
    private AccessLevelModifier modifier;
    private boolean staticAttribute;

    /**
     * Basic constructor, creates the attribute. Sets access level modifier to NO_MODIFIER, final and static to false.
     * @param identifier is the name of the attribute.
     * @param fullName is the full name of type of the attribute, like "java.lang.String", "java.util.list" and "char".
     * @param arrayCount is the amount of array dimensions, should be 0 if it is not an array.
     */
    public ExtractedAttribute(String identifier, String fullName, int arrayCount) {
        super(identifier, fullName, arrayCount);
        modifier = AccessLevelModifier.NO_MODIFIER;
        staticAttribute = false;
        finalAttribute = false;
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
    public void setFlags(AccessLevelModifier modifier, boolean staticAttribute, boolean finalAttribute) {
        this.modifier = modifier;
        this.staticAttribute = staticAttribute;
        this.finalAttribute = finalAttribute;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + modifier + " " + getFullType() + " " + getIdentifier() + ")";
    }
}