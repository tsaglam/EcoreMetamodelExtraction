package eme.parser;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;

import eme.model.ExtractedType;
import eme.model.datatypes.ExtractedAttribute;
import eme.model.datatypes.ExtractedDataType;
import eme.model.datatypes.ExtractedTypeParameter;
import eme.model.datatypes.ExtractedParameter;

/**
 * Helper class to deal with type signatures and generate data types. Parses fields, parameters and return types.
 * @author Timur Saglam
 */
public abstract class DataTypeParser {

    /**
     * Creates extracted data type from a signature and a declaring type. Use this method if the other methods of the
     * class do not fit your needs (e.g. for throws declarations).
     * @param signature is the signature of the data type.
     * @param declaringType is the declaring type of the signature.
     * @return the extracted data type.
     * @throws JavaModelException if there are problems with the JDT API.
     */
    public static ExtractedDataType parseDataType(String signature, IType declaringType) throws JavaModelException {
        int arrayCount = Signature.getArrayCount(signature);
        ExtractedDataType dataType = new ExtractedDataType(getFullName(signature, declaringType), arrayCount);
        dataType.setGenericArguments(parseGenericTypes(signature, declaringType));
        return dataType;
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
        int arrayCount = Signature.getArrayCount(signature);
        String name = field.getElementName(); // name of the field
        ExtractedAttribute attribute = new ExtractedAttribute(name, getFullName(signature, iType), arrayCount);
        attribute.setGenericArguments(parseGenericTypes(signature, iType));
        return attribute;
    }

    /**
     * Parses all type parameters of an IType and adds the to an ExtractedType.
     * @param iType is the IType.
     * @param type is the ExtractedType.
     * @throws @throws JavaModelException if there are problems with the JDT API.
     */
    public static void parseTypeParameters(IType iType, ExtractedType type) throws JavaModelException {
        ExtractedTypeParameter parameter;
        for (String signature : iType.getTypeParameterSignatures()) { // for every type parameter
            parameter = new ExtractedTypeParameter((Signature.getTypeVariable(signature))); // create representation
            for (String bound : Signature.getTypeParameterBounds(signature)) { // if has bound:
                parameter.add(parseDataType(bound, iType)); // add to representation
            }
            type.addMethod(parameter); // add to extracted type
        }
    }

    /**
     * Creates extracted method parameter from a parameter and its method.
     * @param variable is the parameter.
     * @param iMethod is the method of the parameter.
     * @return the extracted method parameter.
     * @throws JavaModelException if there are problems with the JDT API.
     */
    public static ExtractedParameter parseParameter(ILocalVariable variable, IMethod iMethod) throws JavaModelException {
        String signature = variable.getTypeSignature(); // get return type signature
        String name = variable.getElementName(); // name of the parameter
        IType declaringType = iMethod.getDeclaringType(); // declaring type of the method
        int arrayCount = Signature.getArrayCount(signature); // amount of array dimensions
        ExtractedParameter parameter = new ExtractedParameter(name, getFullName(signature, declaringType), arrayCount);
        parameter.setGenericArguments(parseGenericTypes(signature, declaringType));
        return parameter;
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
        return parseDataType(signature, iMethod.getDeclaringType());
    }

    /**
     * Returns the full name of a signature and the declaring type, e.g "java.lang.String", "java.util.List" or "char".
     */
    private static String getFullName(String signature, IType declaringType) throws JavaModelException {
        String simpleName = Signature.getSignatureSimpleName(Signature.getElementType(signature)); // plain name
        String[][] resolvedType = declaringType.resolveType(simpleName); // resolve type from name
        if (resolvedType != null && resolvedType[0] != null) { // if it has full name:
            return Signature.toQualifiedName(resolvedType[0]); // return full name
        } else if (simpleName.contains("<")) { // else return simple name:
            return simpleName.substring(0, simpleName.indexOf('<')); // without generic arguments if it has some
        }
        return simpleName; // or as it is if it has no generic arguments.
    }

    /**
     * Parses generic types from type signature and returns them in a list.
     */
    private static List<ExtractedDataType> parseGenericTypes(String signature, IType declaringType) throws JavaModelException {
        List<ExtractedDataType> genericTypes = new LinkedList<ExtractedDataType>();
        for (String genericTypeSignature : Signature.getTypeArguments(signature)) { // for every argument
            genericTypes.add(parseDataType(genericTypeSignature, declaringType)); // add generic type argument
        }
        return genericTypes;
    }
}