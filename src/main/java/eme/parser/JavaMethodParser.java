package eme.parser;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import eme.model.ExtractedMethod;
import eme.model.ExtractedType;
import eme.model.MethodType;

/**
 * Parser class for Java Methods (Methods, parameters, return types, throws declarations). Uses the class
 * {@link DataTypeParser}.
 * @author Timur Saglam
 */
public class JavaMethodParser {
    private final DataTypeParser dataTypeParser;

    /**
     * Basic constructor.
     * @param dataTypeParser sets the DataTypeParser.
     */
    public JavaMethodParser(DataTypeParser dataTypeParser) {
        this.dataTypeParser = dataTypeParser;
    }

    /**
     * Parses the {@link IMethod}s from an {@link IType} and adds them to an ExtractedType.
     * @param iType is the {@link IType} whose methods get parsed.
     * @param extractedType is the extracted type where the extracted methods should be added.
     * @throws JavaModelException if there are problem with the JDT API.
     */
    public void parseMethods(IType iType, ExtractedType extractedType) throws JavaModelException {
        ExtractedMethod extractedMethod;
        String methodName; // name of the extracted method
        for (IMethod method : iType.getMethods()) { // for every method
            methodName = iType.getFullyQualifiedName() + "." + method.getElementName(); // build name
            extractedMethod = new ExtractedMethod(methodName, dataTypeParser.parseReturnType(method));
            extractedMethod.setAbstract(JDTAdapter.isAbstract(method));
            extractedMethod.setStatic(JDTAdapter.isStatic(method));
            extractedMethod.setMethodType(parseMethodType(method));
            extractedMethod.setModifier(JDTAdapter.getModifier(method));
            for (ILocalVariable parameter : method.getParameters()) { // parse parameters:
                extractedMethod.addParameter(dataTypeParser.parseParameter(parameter, method));
            }
            for (String exception : method.getExceptionTypes()) { // parse throw declarations:
                extractedMethod.addThrowsDeclaration(dataTypeParser.parseDataType(exception, iType));
            }
            extractedType.addMethod(extractedMethod);
        }
    }

    /**
     * Checks whether a {@link IMethod} is an access method (either an accessor or an mutator, depending on the prefix).
     */
    private boolean isAccessMethod(String prefix, IMethod method) throws JavaModelException {
        IType type = method.getDeclaringType();
        for (IField field : type.getFields()) { // for ever field of IType:
            if (method.getElementName().equalsIgnoreCase(prefix + field.getElementName())) {
                return true; // is access method if name scheme fits for one field
            }
        }
        return false; // is not an access method if no field fits
    }

    /**
     * Checks whether a {@link IMethod} is an accessor method.
     */
    private boolean isAccessor(IMethod method) throws JavaModelException {
        if (isAccessMethod("get", method) || isAccessMethod("is", method)) { // if name fits
            return method.getNumberOfParameters() == 0 && !JDTAdapter.isVoid(method.getReturnType());
        }
        return false;
    }

    /**
     * Checks whether a {@link IMethod} is a mutator method.
     */
    private boolean isMutator(IMethod method) throws JavaModelException {
        if (isAccessMethod("set", method)) { // if name fits
            return method.getNumberOfParameters() == 1 && JDTAdapter.isVoid(method.getReturnType());
        }
        return false;
    }

    /**
     * Parses the {@link MethodType} of an {@link IMethod}.
     */
    private MethodType parseMethodType(IMethod method) throws JavaModelException {
        if (method.isConstructor()) {
            return MethodType.CONSTRUCTOR;
        } else if (isAccessor(method)) {
            return MethodType.ACCESSOR;
        } else if (isMutator(method)) {
            return MethodType.MUTATOR;
        } else if (method.isMainMethod()) {
            return MethodType.MAIN;
        }
        return MethodType.NORMAL;
    }
}