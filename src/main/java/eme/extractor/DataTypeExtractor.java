package eme.extractor;

import static eme.extractor.JDTUtil.getName;
import static eme.extractor.JDTUtil.getWildcardStatus;
import static eme.extractor.JDTUtil.hasGenericArguments;
import static eme.extractor.JDTUtil.hasLowerBound;
import static eme.extractor.JDTUtil.hasUpperBound;
import static eme.extractor.JDTUtil.isNestedType;
import static eme.extractor.JDTUtil.isUnresolved;
import static eme.extractor.JDTUtil.isVoid;
import static eme.extractor.JDTUtil.removeGenericArguments;

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
import org.eclipse.jdt.core.ITypeParameter;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;

import eme.handlers.ExtractAndSaveHandler;
import eme.model.datatypes.ExtractedDataType;
import eme.model.datatypes.ExtractedField;
import eme.model.datatypes.ExtractedParameter;
import eme.model.datatypes.ExtractedTypeParameter;

/**
 * Helper class to deal with type signatures and generate data types. Parses fields, parameters and return types.
 * @author Timur Saglam
 */
public class DataTypeExtractor {
    private static final Logger logger = LogManager.getLogger(DataTypeExtractor.class.getName());
    private final Set<String> dataTypes;

    /**
     * Basic constructor, sets the set for the potential external types.
     */
    public DataTypeExtractor() {
        this.dataTypes = new HashSet<String>();
    }

    /**
     * Creates {@link ExtractedDataType} from a signature and a declaring {@link IType}. Use this method if the other
     * methods of the class do not fit your needs (e.g. for throws declarations).
     * @param signature is the signature of the data type.
     * @param declaringType is the declaring {@link IType} of the signature.
     * @return the extracted data type.
     * @throws JavaModelException if there are problems with the JDT API.
     */
    public ExtractedDataType extractDataType(String signature, IType declaringType) throws JavaModelException {
        int arrayCount = Signature.getArrayCount(signature);
        ExtractedDataType dataType = new ExtractedDataType(getFullName(signature, declaringType), arrayCount);
        dataType.setGenericArguments(extractGenericArguments(signature, declaringType));
        return dataType;
    }

    /**
     * Creates {@link ExtractedField} from a {@link IField} and its {@link IType}.
     * @param field is the field.
     * @param type is the type of the field.
     * @return the extracted attribute of the field.
     * @throws JavaModelException if there are problems with the JDT API.
     */
    public ExtractedField extractField(IField field, IType type) throws JavaModelException {
        String signature = field.getTypeSignature(); // get return type signature
        int arrayCount = Signature.getArrayCount(signature);
        String name = field.getElementName(); // name of the field
        ExtractedField extractedField = new ExtractedField(name, getFullName(signature, type), arrayCount);
        extractedField.setGenericArguments(extractGenericArguments(signature, type));
        return extractedField;
    }

    /**
     * Creates {@link ExtractedParameter} from a {@link ILocalVariable} and its {@link IMethod}.
     * @param variable is the parameter.
     * @param iMethod is the method of the parameter.
     * @return the extracted method parameter.
     * @throws JavaModelException if there are problems with the JDT API.
     */
    public ExtractedParameter extractParameter(ILocalVariable variable, IMethod iMethod) throws JavaModelException {
        String signature = variable.getTypeSignature(); // get return type signature
        String name = variable.getElementName(); // name of the parameter
        IType declaringType = iMethod.getDeclaringType(); // declaring type of the method
        int arrayCount = Signature.getArrayCount(signature); // amount of array dimensions
        ExtractedParameter parameter = new ExtractedParameter(name, getFullName(signature, declaringType), arrayCount);
        parameter.setGenericArguments(extractGenericArguments(signature, declaringType));
        return parameter;
    }

    /**
     * Creates extracted return type from a {@link IMethod}.
     * @param iMethod is the method.
     * @return the return type, or null if it is void.
     * @throws JavaModelException if there are problems with the JDT API.
     */
    public ExtractedDataType extractReturnType(IMethod iMethod) throws JavaModelException {
        String signature = iMethod.getReturnType(); // get return type signature
        if (isVoid(signature)) {
            return null; // void signature, no return type.
        }
        return extractDataType(signature, iMethod.getDeclaringType());
    }

    /**
     * Generates a list of {@link ExtractedTypeParameter}s from an array of {@link ITypeParameter}s and the declaring
     * {@link IType}. This method exists because {@link IMethod} and {@link IType} do not have a common super type for
     * getTypeParameters()
     * @param typeParameters is the array of {@link ITypeParameter}s.
     * @param declaringType is either the {@link IType} that owns the {@link ITypeParameter}s or the declaring type of
     * the {@link IMethod} that owns the {@link ITypeParameter}s.
     * @return list of {@link ExtractedTypeParameter}s.
     * @throws JavaModelException if there are problems with the JDT API.
     */
    public List<ExtractedTypeParameter> extractTypeParameters(ITypeParameter[] typeParameters, IType declaringType) throws JavaModelException {
        List<ExtractedTypeParameter> parameterList = new LinkedList<ExtractedTypeParameter>();
        for (ITypeParameter typeParameter : typeParameters) { // for every type parameter
            ExtractedTypeParameter parameter = new ExtractedTypeParameter(typeParameter.getElementName());
            extractBounds(parameter, typeParameter.getBoundsSignatures(), declaringType);
            parameterList.add(parameter); // add to extracted type
        }
        return parameterList;
    }

    /**
     * Returns a copy of the set of potential external type names.
     * @return the new set of type names.
     */
    public Set<String> getDataTypes() {
        return new HashSet<String>(dataTypes);
    }

    /**
     * Extracts {@link ExtractAndSaveHandler} bounds for an {@link ExtractedTypeParameter} from an array of bound
     * signatures. Needs an declaring type, which is the {@link IType} itself or the declaring type of an
     * {@link IMethod}.
     */
    private void extractBounds(ExtractedTypeParameter parameter, String[] signatures, IType declaringType) throws JavaModelException {
        for (String bound : signatures) { // if has bound:
            parameter.add(extractDataType(bound, declaringType)); // add to type parameter
        }
    }

    /**
     * Parses generic arguments from signature and returns them in a list.
     */
    private List<ExtractedDataType> extractGenericArguments(String signature, IType declaringType) throws JavaModelException {
        List<ExtractedDataType> genericArguments = new LinkedList<ExtractedDataType>();
        for (String argumentSignature : Signature.getTypeArguments(signature)) { // for every argument
            ExtractedDataType genericArgument = extractDataType(argumentSignature, declaringType);
            genericArgument.setWildcardStatus(getWildcardStatus(argumentSignature));
            genericArguments.add(genericArgument); // add generic type argument
        }
        return genericArguments;
    }

    /**
     * Tries to resolve an unresolved type signature.
     */
    private String extractUnresolved(String signature, IType declaringType) throws JavaModelException {
        String typeName = signature.substring(1, signature.length() - 1); // cut signature symbols
        if (hasGenericArguments(typeName)) {
            typeName = removeGenericArguments(typeName);
        }
        if (isNestedType(typeName)) { // if is inner type
            typeName = resolveInnerType(typeName, declaringType); // try to resolve it manually
        }
        return typeName; // return type name
    }

    /**
     * Returns the full name of a signature and the declaring {@link IType}, e.g "java.lang.String", "java.util.List" or
     * "char".
     */
    private String getFullName(String typeSignature, IType declaringType) throws JavaModelException {
        String signature = Signature.getElementType(typeSignature); // remove array information
        if (hasLowerBound(signature) || hasUpperBound(signature)) {
            signature = signature.substring(1); // remove wild card parameter
        }
        String name = Signature.getSignatureSimpleName(signature); // get plain name
        String[][] resolvedType = declaringType.resolveType(name); // resolve type from name
        if (resolvedType != null && resolvedType[0] != null) { // if it has full name:
            name = Signature.toQualifiedName(resolvedType[0]); // generate full qualified name
        } else if (isUnresolved(signature)) { // if not resolved
            name = extractUnresolved(signature, declaringType); // try to resolve manually
        }
        dataTypes.add(name); // potential external type
        return name;
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
                    logger.warn("Resolved type " + getName(resolvedType) + " through import declarations!");
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
        String declaringTypeName = getName(declaringType); // get parent name
        IJavaProject project = declaringType.getPackageFragment().getJavaProject(); // try to resolve locally:
        IType type = project.findType(declaringTypeName.substring(0, declaringTypeName.lastIndexOf('.')), innerType);
        if (type == null) { // if still not resolved
            type = resolveFromImports(innerType, declaringType); // try resolving it from import
        }
        if (type != null) { // if is resolved (one way or another)
            return getName(type); // return resolved name
        } // else:
        return innerType; // return unresolved name
    }
}