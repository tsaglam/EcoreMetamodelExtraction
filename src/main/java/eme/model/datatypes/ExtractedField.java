package eme.model.datatypes;

import eme.model.IntermediateModel;

/**
 * Represents an extracted Attribute in {@link IntermediateModel}
 * @author Timur Saglam
 */
public class ExtractedField extends ExtractedVariable {
    private boolean finalAttribute;
    private AccessLevelModifier modifier;
    private boolean staticAttribute;

    /**
     * Basic constructor, creates the attribute. Sets access level modifier to {@link AccessLevelModifier}.NO_MODIFIER,
     * final and static to false.
     * @param identifier is the name of the attribute.
     * @param fullTypeName is the full name of type of the attribute, like "java.lang.String", "java.util.list" and
     * "char".
     * @param arrayDimension is the amount of array dimensions, should be 0 if it is not an array.
     */
    public ExtractedField(String identifier, String fullTypeName, int arrayDimension) {
        super(identifier, fullTypeName, arrayDimension);
        modifier = AccessLevelModifier.NO_MODIFIER;
        staticAttribute = false;
        finalAttribute = false;
    }

    /**
     * accessor for the access level modifier.
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
     * Mutator for the property final.
     * @param finalAttribute determines whether the attribute is final or not.
     */
    public void setFinal(boolean finalAttribute) {
        this.finalAttribute = finalAttribute;
    }

    /**
     * Mutator for the {@link AccessLevelModifier} of the attribute.
     * @param modifier is the access level modifier.
     */
    public void setModifier(AccessLevelModifier modifier) {
        this.modifier = modifier;
    }

    /**
     * Mutator for the property static.
     * @param staticAttribute determines whether the attribute is static or not.
     */
    public void setStatic(boolean staticAttribute) {
        this.staticAttribute = staticAttribute;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + modifier + " " + getFullType() + " " + getIdentifier() + ")";
    }
}