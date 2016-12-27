package eme.parser;

import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;

/**
 * Helper class to deal with type signatures.
 * @author Timur Saglam
 */
public abstract class TypeParser {
    public static String parse(ILocalVariable parameterVariable, IType declaringType) {
        
        return null;
    }
    
    /**
     * Returns the simple name of a type signature, e.g "String", "List<int>" or char[][].
     * @param typeSignatue is the type signature to translate.
     * @return the simple name.
     */
    public static String simpleName(String typeSignatue) {
        return Signature.toString(typeSignatue);
    }

    public static String fullName(ILocalVariable parameterVariable, IMethod method) throws JavaModelException {
        IType declaringType = method.getDeclaringType();
        String name = parameterVariable.getTypeSignature();
        String simpleName = Signature.getSignatureSimpleName(name);
        String[][] allResults = declaringType.resolveType(simpleName);
        String fullName = "";
        if (allResults != null) {
            String[] nameParts = allResults[0];
            if (nameParts != null) {
                for (int i = 0; i < nameParts.length; i++) {
                    if (fullName.length() > 0) {
                        fullName += '.';
                    }
                    String part = nameParts[i];
                    if (part != null) {
                        fullName += part;
                    }
                }
            }
        }
        return fullName;
    }
}
