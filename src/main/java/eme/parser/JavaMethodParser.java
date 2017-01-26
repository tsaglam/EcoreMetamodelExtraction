package eme.parser;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;

import eme.model.ExtractedMethod;
import eme.model.ExtractedType;
import eme.model.MethodType;
import eme.model.datatypes.AccessLevelModifier;

/**
 * Parser class for Java Methods (Methods, parameters, return types, throws declarations).
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
     * Parses the methods from an IType and adds them to an ExtractedType.
     * @param iType is the IType whose methods get parsed.
     * @param extractedType is the extracted type where the extracted methods should be added.
     * @throws JavaModelException if there are problem with the JDT API.
     */
    public void parseMethods(IType iType, ExtractedType extractedType) throws JavaModelException {
        ExtractedMethod extractedMethod;
        String methodName; // name of the extracted method
        for (IMethod method : iType.getMethods()) { // for every method
            methodName = iType.getFullyQualifiedName() + "." + method.getElementName(); // build name
            int flags = method.getFlags();
            extractedMethod = new ExtractedMethod(methodName, dataTypeParser.parseReturnType(method));
            extractedMethod.setFlags(AccessLevelModifier.getFrom(flags), parseMethodType(method), Flags.isStatic(flags), Flags.isAbstract(flags));
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
     * Checks whether a IMethod is an access method (either an accessor or an mutator, depending on the prefix).
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
     * Checks whether a IMethod is an accessor method.
     */
    private boolean isAccessor(IMethod method) throws JavaModelException {
        if (isAccessMethod("get", method) || isAccessMethod("is", method)) { // if name fits
            return method.getNumberOfParameters() == 0 && !Signature.SIG_VOID.equals(method.getReturnType());
        }
        return false;
    }

    /**
     * Checks whether a IMethod is a mutator method.
     */
    private boolean isMutator(IMethod method) throws JavaModelException {
        if (isAccessMethod("set", method)) { // if name fits
            return method.getNumberOfParameters() == 1 && Signature.SIG_VOID.equals(method.getReturnType());
        }
        return false;
    }

    /**
     * Parses the MethodType of an IMethod.
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