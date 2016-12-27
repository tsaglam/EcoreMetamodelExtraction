package eme.parser;

import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;

/**
 * Helper class to deal with type signatures. Used following source:
 * stackoverflow.com/questions/27775320/how-to-get-fully-qualified-name-of-parameter-value-in-a-method
 * @author Timur Saglam
 */
public abstract class TypeParser {
    /**
     * Returns the full name of a parameter, e.g "java.lang.String", "java.util.List" or "char".
     * @param parameterVariable is the parameter.
     * @param method is the method where the parameter belongs.
     * @return the full name.
     * @throws JavaModelException if there are problems with the JDT API.
     */
    public static String fullName(ILocalVariable parameterVariable, IMethod method) throws JavaModelException {
        String simpleName = Signature.getSignatureSimpleName(parameterVariable.getTypeSignature());
        String[][] resolvedTypeNames = method.getDeclaringType().resolveType(simpleName);
        String fullName = ""; // TODO (middle) design own approach
        if (resolvedTypeNames != null) {
            String[] typeName = resolvedTypeNames[0];
            if (typeName != null) {
                for (int i = 0; i < typeName.length; i++) {
                    if (fullName.length() > 0) {
                        fullName += '.';
                    }
                    String part = typeName[i];
                    if (part != null) {
                        fullName += part;
                    }
                }
            }
        }
        return fullName;
    }

    /**
     * Returns the simple name of a type signature, e.g "String", "List<int>" or "char[][]".
     * @param typeSignatue is the type signature to translate.
     * @return the simple name.
     */
    public static String simpleName(String typeSignatue) {
        return Signature.getSignatureSimpleName(typeSignatue);
    }
}
