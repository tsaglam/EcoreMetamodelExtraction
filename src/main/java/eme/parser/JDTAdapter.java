package eme.parser;

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
public final class JDTAdapter { // TODO (HIGH) comments

    /**
     * Private constructor for static class.
     */
    private JDTAdapter() {
        // Private constructor.
    }

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

    public static String getName(IType iType) {
        return iType.getFullyQualifiedName('.');
    }

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

    public static boolean hasGenericArguments(String name) {
        return name.contains(Character.toString(Signature.C_GENERIC_START));
    }

    public static boolean hasLowerBound(String signature) {
        return signature.charAt(0) == Signature.C_SUPER;
    }

    public static boolean hasUpperBound(String signature) {
        return signature.charAt(0) == Signature.C_EXTENDS;
    }

    public static boolean isAbstract(IMember iMember) throws JavaModelException {
        return Flags.isAbstract(iMember.getFlags());
    }

    public static boolean isEnum(IMember iMember) throws JavaModelException {
        return Flags.isEnum(iMember.getFlags());
    }

    public static boolean isFinal(IMember iMember) throws JavaModelException {
        return Flags.isFinal(iMember.getFlags());
    }

    public static boolean isNestedType(String name) {
        return name.contains(".") && Character.isUpperCase(name.charAt(0));
    }

    public static boolean isStatic(IMember iMember) throws JavaModelException {
        return Flags.isStatic(iMember.getFlags());
    }

    public static boolean isUnresolved(String signature) {
        return signature.charAt(0) == Signature.C_UNRESOLVED;
    }

    public static boolean isVoid(String signature) {
        return Signature.SIG_VOID.equals(signature);
    }

    public static String removeGenericArguments(String name) {
        return name.substring(0, name.lastIndexOf(Signature.C_GENERIC_START));
    }
}