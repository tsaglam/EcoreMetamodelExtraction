package eme.parser;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;

import eme.model.ExtractedAttribute;
import eme.model.ExtractedDataType;
import eme.model.ExtractedVariable;

/**
 * Helper class to deal with type signatures. Used following source:
 * stackoverflow.com/questions/27775320/how-to-get-fully-qualified-name-of-parameter-value-in-a-method
 * @author Timur Saglam
 */
public abstract class TypeParser {

    /**
     * Returns the full name of a parameter, e.g "java.lang.String", "java.util.List" or "char".
     * @param typeSignature is the type signature.
     * @param method is the method where the parameter belongs.
     * @return the full name.
     * @throws JavaModelException if there are problems with the JDT API.
     */
    public static String fullName(String typeSignature, IMethod method) throws JavaModelException {
        return fullName(typeSignature, method.getDeclaringType());
    }

    /**
     * Returns the full name of a field, e.g "java.lang.String", "java.util.List" or "char".
     * @param typeSignature is the type signature.
     * @param iType is the type where the field belongs to.
     * @return the full name.
     * @throws JavaModelException if there are problems with the JDT API.
     */
    public static String fullName(String signature, IType iType) throws JavaModelException {
        String typeSignature = Signature.getElementType(signature);
        String simpleName = Signature.getSignatureSimpleName(typeSignature);
        String[][] resolvedTypeNames = iType.resolveType(simpleName);
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
        return ("".equals(fullName)) ? simpleName : fullName;
    }

    /**
     * Creates extracted attribute from a field and its type.
     * @param field is the field.
     * @param iType is the type of the field.
     * @return the extracted attribute of the field.
     * @throws JavaModelException if there are problems with the JDT API.
     */
    public static ExtractedAttribute parseField(IField field, IType iType) throws JavaModelException {
        String signature = field.getTypeSignature(); // get return type signature
        String simpleName = TypeParser.simpleName(signature); // get simple name
        String fullName = TypeParser.fullName(signature, iType); // get full name
        return new ExtractedAttribute(field.getElementName(), simpleName, fullName, Signature.getArrayCount(signature));
    }

    /**
     * Creates extracted method parameter from a parameter and its method.
     * @param parameter is the parameter.
     * @param iMethod is the method of the parameter.
     * @return the extracted method parameter.
     * @throws JavaModelException if there are problems with the JDT API.
     */
    public static ExtractedVariable parseParameter(ILocalVariable parameter, IMethod iMethod) throws JavaModelException {
        String signature = parameter.getTypeSignature(); // get return type signature
        String simpleName = TypeParser.simpleName(signature); // get simple name
        String fullName = TypeParser.fullName(signature, iMethod); // get full name
        return new ExtractedVariable(parameter.getElementName(), simpleName, fullName, Signature.getArrayCount(signature));
    }

    /**
     * Creates extracted return type from a method.
     * @param iMethod is the method.
     * @return the return type, or null if it is void.
     * @throws JavaModelException if there are problems with the JDT API.
     */
    public static ExtractedDataType parseReturnType(IMethod iMethod) throws JavaModelException {
        String signature = iMethod.getReturnType(); // get return type signature
        if (Signature.SIG_VOID.equals(signature)) {
            return null; // void signature, no return type.
        }
        String simpleName = TypeParser.simpleName(signature); // get simple name
        String fullName = TypeParser.fullName(signature, iMethod); // get full name
        return new ExtractedDataType(simpleName, fullName, Signature.getArrayCount(signature)); // create return type
    }

    /**
     * Returns the simple name of a type signature, e.g "String", "List&ltint&gt" or "char[][]".
     * @param typeSignatue is the type signature to translate.
     * @return the simple name.
     */
    public static String simpleName(String typeSignatue) { // TODO (MEDIUM) better solution
        String name = Signature.getSignatureSimpleName(typeSignatue);
        for (int i = 0; i < name.length(); i++) {
            if (name.charAt(i) == '<' || name.charAt(i) == '[') {
                return name.substring(0, i);
            }
        }
        return name;
    }
}