package eme.extractor;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;

import eme.model.datatypes.AccessLevelModifier;
import eme.model.datatypes.WildcardStatus;

/**
 * This class offers simple adapter methods for JDT functionality that makes code unreadable.
 * @author Timur Saglam
 */
public final class JDTUtil {

    /**
     * Private constructor for static class.
     */
    private JDTUtil() {
        // Private constructor.
    }

    /**
     * Determines the access level modifier of an {@link IMember} and returns it as {@link AccessLevelModifier}.
     * @param iMember is the {@link IMember}.
     * @return the {@link AccessLevelModifier}.
     * @throws JavaModelException if there is a problem with the JDT API.
     */
    public static AccessLevelModifier getModifier(IMember iMember) throws JavaModelException {
        int flags = iMember.getFlags();
        if (Flags.isPublic(flags)) {
            return AccessLevelModifier.PUBLIC;
        } else if (Flags.isPrivate(flags)) {
            return AccessLevelModifier.PRIVATE;
        } else if (Flags.isProtected(flags)) {
            return AccessLevelModifier.PROTECTED;
        }
        return AccessLevelModifier.NO_MODIFIER;
    }

    /**
     * Returns the fully qualified name of an {@link IType}, including qualification for any containing types and
     * packages. The character '.' is used as enclosing type separator.
     * @param iType is the {@link IType}.
     * @return the fully qualified name.
     * @see eme.generator.saving.AbstractSavingStrategy#filePath()
     */
    public static String getName(IType iType) {
        return iType.getFullyQualifiedName('.');
    }

    /**
     * Determines the {@link WildcardStatus} of an signature string.
     * @param signature is the signature string.
     * @return the {@link WildcardStatus}.
     */
    public static WildcardStatus getWildcardStatus(String signature) {
        if (signature.contains(Character.toString(Signature.C_STAR))) {
            return WildcardStatus.WILDCARD; // is unbound wildcard
        } else if (signature.contains(Character.toString(Signature.C_EXTENDS))) {
            return WildcardStatus.WILDCARD_UPPER_BOUND; // is upper bound wildcard
        } else if (signature.contains(Character.toString(Signature.C_SUPER))) {
            return WildcardStatus.WILDCARD_LOWER_BOUND; // is lower bound wildcard
        } // else:
        return WildcardStatus.NO_WILDCARD; // is no wildcard
    }

    /**
     * Checks if a type name contains generic arguments.
     * @param name is the name of the type.
     * @return true if it contains generic arguments.
     */
    public static boolean hasGenericArguments(String name) {
        return name.contains(Character.toString(Signature.C_GENERIC_START));
    }

    /**
     * Checks if a signature contains a character constant indicating a lower bound wildcard type argument.
     * @param signature is the signature string.
     * @return true if it contains one.
     */
    public static boolean hasLowerBound(String signature) {
        return signature.charAt(0) == Signature.C_SUPER;
    }

    /**
     * Checks if a signature contains a character constant indicating a upper bound wildcard type argument.
     * @param signature is the signature string.
     * @return true if it contains one.
     */
    public static boolean hasUpperBound(String signature) {
        return signature.charAt(0) == Signature.C_EXTENDS;
    }

    /**
     * Checks if a {@link IMember} is abstract by checking its flags.
     * @param iMember is the {@link IMember}.
     * @return true if it is.
     * @throws JavaModelException if there is a problem with the JDT API.
     */
    public static boolean isAbstract(IMember iMember) throws JavaModelException {
        return Flags.isAbstract(iMember.getFlags());
    }

    /**
     * Checks if a {@link IMember} is an enum by checking its flags.
     * @param iMember is the {@link IMember}.
     * @return true if it is.
     * @throws JavaModelException if there is a problem with the JDT API.
     */
    public static boolean isEnum(IMember iMember) throws JavaModelException {
        return Flags.isEnum(iMember.getFlags());
    }

    /**
     * Checks if a {@link IMember} is final by checking its flags.
     * @param iMember is the {@link IMember}.
     * @return true if it is.
     * @throws JavaModelException if there is a problem with the JDT API.
     */
    public static boolean isFinal(IMember iMember) throws JavaModelException {
        return Flags.isFinal(iMember.getFlags());
    }

    /**
     * Checks if a type name is a nested type (OuterType.InnerType).
     * @param name is the name of the type.
     * @return true if it is a nested type.
     */
    public static boolean isNestedType(String name) {
        return name.contains(".") && Character.isUpperCase(name.charAt(0));
    }

    /**
     * Checks if a {@link IMember} is static by checking its flags.
     * @param iMember is the {@link IMember}.
     * @return true if it is.
     * @throws JavaModelException if there is a problem with the JDT API.
     */
    public static boolean isStatic(IMember iMember) throws JavaModelException {
        return Flags.isStatic(iMember.getFlags());
    }

    /**
     * Checks if a signature contains a character constant indicating the start of an unresolved, named type in a
     * signature.
     * @param signature is the signature string.
     * @return true if it contains one.
     */
    public static boolean isUnresolved(String signature) {
        return signature.charAt(0) == Signature.C_UNRESOLVED;
    }

    /**
     * Checks if a signature is the string constant for the signature of result type void.
     * @param signature is the signature string.
     * @return true if it is void.
     */
    public static boolean isVoid(String signature) {
        return Signature.SIG_VOID.equals(signature);
    }

    /**
     * Removes any generic arguments from a type name.
     * @param name is the type name.
     * @return the type name without generic arguments.
     */
    public static String removeGenericArguments(String name) {
        return name.substring(0, name.lastIndexOf(Signature.C_GENERIC_START));
    }
}