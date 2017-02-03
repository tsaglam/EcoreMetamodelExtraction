package eme.parser;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IJavaProject;
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

/**
 * Helper class to deal with type signatures and generate data types. Parses fields, parameters and return types.
 * @author Timur Saglam
 */
public class DataTypeParser {
    private static final Logger logger = LogManager.getLogger(DataTypeParser.class.getName());
    private final Set<String> dataTypes;

    /**
     * Basic constructor, sets the set for the potential external types.
     */
    public DataTypeParser() {
        this.dataTypes = new HashSet<String>();
    }

    /**
     * Returns a copy of the set of potential external type names.
     * @return the new set of type names.
     */
    public Set<String> getDataTypes() {
        return new HashSet<String>(dataTypes);
    }

    /**
     * Creates {@link ExtractedDataType} from a signature and a declaring {@link IType}. Use this method if the other
     * methods of the class do not fit your needs (e.g. for throws declarations).
     * @param signature is the signature of the data type.
     * @param declaringType is the declaring {@link IType} of the signature.
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
     * Creates {@link ExtractedAttribute} from a {@link IField} and its {@link IType}.
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
     * Creates {@link ExtractedParameter} from a {@link ILocalVariable} and its {@link IMethod}.
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
     * Creates extracted return type from a {@link IMethod}.
     * @param iMethod is the method.
     * @return the return type, or null if it is void.
     * @throws JavaModelException if there are problems with the JDT API.
     */
    public ExtractedDataType parseReturnType(IMethod iMethod) throws JavaModelException {
        String signature = iMethod.getReturnType(); // get return type signature
        if (Util.isVoid(signature)) {
            return null; // void signature, no return type.
        }
        return parseDataType(signature, iMethod.getDeclaringType());
    }

    /**
     * Parses all type parameters of an {@link IType} and adds the to an {@link ExtractedType}.
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
     * Returns the full name of a signature and the declaring {@link IType}, e.g "java.lang.String", "java.util.List" or
     * "char".
     */
    private String getFullName(String typeSignature, IType declaringType) throws JavaModelException {
        String signature = Signature.getElementType(typeSignature); // remove array information
        if (Util.hasLowerBound(signature) || Util.hasUpperBound(signature)) {
            signature = signature.substring(1); // remove wild card parameter
        }
        String name = Signature.getSignatureSimpleName(signature); // get plain name
        String[][] resolvedType = declaringType.resolveType(name); // resolve type from name
        if (resolvedType != null && resolvedType[0] != null) { // if it has full name:
            name = Signature.toQualifiedName(resolvedType[0]); // generate full qualified name
        } else if (Util.isUnresolved(signature)) { // if not resolved
            name = parseUnresolved(signature, declaringType); // try to resolve manually
        }
        dataTypes.add(name); // potential external type
        return name; // return type name
    }

    /**
     * Parses generic arguments from signature and returns them in a list.
     */
    private List<ExtractedDataType> parseGenericArguments(String signature, IType declaringType) throws JavaModelException {
        List<ExtractedDataType> genericArguments = new LinkedList<ExtractedDataType>();
        for (String argumentSignature : Signature.getTypeArguments(signature)) { // for every argument
            ExtractedDataType genericArgument = parseDataType(argumentSignature, declaringType);
            genericArgument.setWildcardStatus(Util.getWildcardStatus(argumentSignature));
            genericArguments.add(genericArgument); // add generic type argument
        }
        return genericArguments;
    }

    /**
     * Tries to resolve an unresolved type signature.
     */
    private String parseUnresolved(String signature, IType declaringType) throws JavaModelException {
        String typeName = signature.substring(1, signature.length() - 1); // cut signature symbols
        if (Util.hasGenericArguments(typeName)) {
            typeName = Util.removeGenericArguments(typeName);
        }
        if (Util.isNestedType(typeName)) { // if is inner type
            typeName = resolveInnerType(typeName, declaringType); // try to resolve it manually
        }
        return typeName; // return type name
    }

    /**
     * Checks the compilation unit of the declaring type of the unresolved type for package declarations that help to
     * find the IType.
     */
    private IType resolveFromImports(String typeName, IType declaringType) throws JavaModelException {
        ICompilationUnit unit = declaringType.getCompilationUnit();
        IJavaProject project = declaringType.getPackageFragment().getJavaProject(); // project
        for (IImportDeclaration importDeclaration : unit.getImports()) {
            String name = importDeclaration.getElementName();
            if (name.contains(typeName.split("\\.")[0])) { // if package declaration contains outer type
                IType resolvedType = project.findType(name.substring(0, name.lastIndexOf('.')), typeName);
                if (resolvedType != null) { // if resolved an existing IType
                    logger.warn("Resolved type " + Util.getName(resolvedType) + " through import declarations!");
                    return resolvedType; // was successful
                }
            }
        }
        return null;
    }

    /**
     * Tries to resolve an unresolved inner type (e.g. "Outer.Inner") and return its full name.
     */
    private String resolveInnerType(String innerType, IType declaringType) throws JavaModelException {
        String declaringTypeName = Util.getName(declaringType); // get parent name
        IJavaProject project = declaringType.getPackageFragment().getJavaProject(); // try to resolve locally:
        IType iType = project.findType(declaringTypeName.substring(0, declaringTypeName.lastIndexOf('.')), innerType);
        if (iType == null) { // if still not resolved
            iType = resolveFromImports(innerType, declaringType); // try resolving it from import
        }
        if (iType != null) { // if is resolved (one way or another)
            return Util.getName(iType); // return resolved name
        } // else:
        return innerType; // return unresolved name
    }
}