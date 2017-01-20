package eme.parser;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;

import eme.model.ExtractedType;
import eme.model.datatypes.ExtractedAttribute;
import eme.model.datatypes.ExtractedDataType;
import eme.model.datatypes.ExtractedParameter;
import eme.model.datatypes.ExtractedTypeParameter;
import eme.model.datatypes.WildcardStatus;

/**
 * Helper class to deal with type signatures and generate data types. Parses fields, parameters and return types.
 * @author Timur Saglam
 */
public class DataTypeParser {
    private final Set<String> dataTypes;

    /**
     * Basic constructor, sets the set for the potential external types.
     */
    public DataTypeParser() {
        this.dataTypes = new HashSet<String>();
    }

    /**
     * Returns the set of potential external types.
     * @return the set of type names.
     */
    public Set<String> getPotentialExternalTypes() {
        return new HashSet<String>(dataTypes);
    }

    /**
     * Creates extracted data type from a signature and a declaring type. Use this method if the other methods of the
     * class do not fit your needs (e.g. for throws declarations).
     * @param signature is the signature of the data type.
     * @param declaringType is the declaring type of the signature.
     * @return the extracted data type.
     * @throws JavaModelException if there are problems with the JDT API.
     */
    public ExtractedDataType parseDataType(String signature, IType declaringType) throws JavaModelException {
        int arrayCount = Signature.getArrayCount(signature);
        ExtractedDataType dataType = new ExtractedDataType(getFullName(signature, declaringType), arrayCount);
        dataType.setGenericArguments(parseGenericArguments(signature, declaringType));
        return dataType;
    }

    /**
     * Creates extracted attribute from a field and its type.
     * @param field is the field.
     * @param iType is the type of the field.
     * @return the extracted attribute of the field.
     * @throws JavaModelException if there are problems with the JDT API.
     */
    public ExtractedAttribute parseField(IField field, IType iType) throws JavaModelException {
        String signature = field.getTypeSignature(); // get return type signature
        int arrayCount = Signature.getArrayCount(signature);
        String name = field.getElementName(); // name of the field
        ExtractedAttribute attribute = new ExtractedAttribute(name, getFullName(signature, iType), arrayCount);
        attribute.setGenericArguments(parseGenericArguments(signature, iType));
        return attribute;
    }

    /**
     * Creates extracted method parameter from a parameter and its method.
     * @param variable is the parameter.
     * @param iMethod is the method of the parameter.
     * @return the extracted method parameter.
     * @throws JavaModelException if there are problems with the JDT API.
     */
    public ExtractedParameter parseParameter(ILocalVariable variable, IMethod iMethod) throws JavaModelException {
        String signature = variable.getTypeSignature(); // get return type signature
        String name = variable.getElementName(); // name of the parameter
        IType declaringType = iMethod.getDeclaringType(); // declaring type of the method
        int arrayCount = Signature.getArrayCount(signature); // amount of array dimensions
        ExtractedParameter parameter = new ExtractedParameter(name, getFullName(signature, declaringType), arrayCount);
        parameter.setGenericArguments(parseGenericArguments(signature, declaringType));
        return parameter;
    }

    /**
     * Creates extracted return type from a method.
     * @param iMethod is the method.
     * @return the return type, or null if it is void.
     * @throws JavaModelException if there are problems with the JDT API.
     */
    public ExtractedDataType parseReturnType(IMethod iMethod) throws JavaModelException {
        String signature = iMethod.getReturnType(); // get return type signature
        if (Signature.SIG_VOID.equals(signature)) {
            return null; // void signature, no return type.
        }
        return parseDataType(signature, iMethod.getDeclaringType());
    }

    /**
     * Parses all type parameters of an IType and adds the to an ExtractedType.
     * @param iType is the IType.
     * @param type is the ExtractedType.
     * @throws JavaModelException if there are problems with the JDT API.
     */
    public void parseTypeParameters(IType iType, ExtractedType type) throws JavaModelException {
        ExtractedTypeParameter parameter;
        for (String signature : iType.getTypeParameterSignatures()) { // for every type parameter
            parameter = new ExtractedTypeParameter(Signature.getTypeVariable(signature)); // create representation
            for (String bound : Signature.getTypeParameterBounds(signature)) { // if has bound:
                parameter.add(parseDataType(bound, iType)); // add to representation
            }
            type.addTypeParameter(parameter); // add to extracted type
        }
    }

    /**
     * Returns the full name of a signature and the declaring type, e.g "java.lang.String", "java.util.List" or "char".
     */
    private String getFullName(String typeSignature, IType declaringType) throws JavaModelException {
        String signature = Signature.getElementType(typeSignature); // remove array information
        if (signature.charAt(0) == Signature.C_SUPER || signature.charAt(0) == Signature.C_EXTENDS) {
            signature = signature.substring(1); // remove wild card parameter
        }
        String name = Signature.getSignatureSimpleName(signature); // get plain name
        String[][] resolvedType = declaringType.resolveType(name); // resolve type from name
        if (resolvedType != null && resolvedType[0] != null) { // if it has full name:
            name = Signature.toQualifiedName(resolvedType[0]); // generate full qualified name
        } else if (signature.charAt(0) == Signature.C_UNRESOLVED) { // if not resolved
            name = signature.substring(1, signature.length() - 1); // cut signature symbols
        }
        dataTypes.add(name); // potential external type
        return name; // return type name
    }

    /**
     * Parses generic arguments from type signature and returns them in a list.
     */
    private List<ExtractedDataType> parseGenericArguments(String signature, IType declaringType) throws JavaModelException {
        List<ExtractedDataType> genericArguments = new LinkedList<ExtractedDataType>();
        for (String argumentSignature : Signature.getTypeArguments(signature)) { // for every argument
            ExtractedDataType genericArgument = parseDataType(argumentSignature, declaringType);
            genericArgument.setWildcardStatus(parseWildcardStatus(argumentSignature));
            genericArguments.add(genericArgument); // add generic type argument
        }
        return genericArguments;
    }

    /**
     * Parses signature and return wild card status.
     */
    private WildcardStatus parseWildcardStatus(String signature) {
        if (signature.contains(Character.toString(Signature.C_STAR))) {
            return WildcardStatus.WILDCARD;
        } else if (signature.contains(Character.toString(Signature.C_EXTENDS))) {
            return WildcardStatus.WILDCARD_UPPER_BOUND;
        } else if (signature.contains(Character.toString(Signature.C_SUPER))) {
            return WildcardStatus.WILDCARD_LOWER_BOUND;
        }
        return WildcardStatus.NO_WILDCARD;
    }
}