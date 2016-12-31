package eme.model.datatypes;

import org.eclipse.jdt.core.Flags;

/**
 * Enum for access level modifiers.
 * @author Timur Saglam
 */
public enum AccessLevelModifier {
    NO_MODIFIER, PUBLIC, PRIVATE, PROTECTED;

    /**
     * Generate the access level modifier from an flags integer (see {@link org.eclipse.jdt.core.Flags})
     * @param flags is the flag integer.
     * @return the access level modifier.
     */
    public static AccessLevelModifier getFrom(int flags) {
        if (Flags.isPublic(flags)) {
            return PUBLIC;
        } else if (Flags.isPrivate(flags)) {
            return PRIVATE;
        } else if (Flags.isProtected(flags)) {
            return PROTECTED;
        }
        return NO_MODIFIER;
    }
}