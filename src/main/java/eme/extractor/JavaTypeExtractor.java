package eme.extractor;

import static eme.extractor.JDTUtil.getModifier;
import static eme.extractor.JDTUtil.getName;
import static eme.extractor.JDTUtil.isAbstract;
import static eme.extractor.JDTUtil.isEnum;
import static eme.extractor.JDTUtil.isFinal;
import static eme.extractor.JDTUtil.isStatic;

import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;

import eme.model.ExtractedClass;
import eme.model.ExtractedEnumeral;
import eme.model.ExtractedEnumeration;
import eme.model.ExtractedInterface;
import eme.model.ExtractedType;
import eme.model.IntermediateModel;
import eme.model.datatypes.ExtractedAttribute;

/**
 * Parser class for Java types (classes, interfaces, enumerations). The class uses the {@link JavaMethodExtractor} and
 * the {@link DataTypeExtractor}.
 * @author Timur Saglam
 */
public class JavaTypeExtractor {
    private static final Logger logger = LogManager.getLogger(JavaTypeExtractor.class.getName());
    private final DataTypeExtractor dataTypeParser;
    private final JavaMethodExtractor methodParser;
    private final IntermediateModel model;
    private final IJavaProject project;

    /**
     * Basic constructor.
     * @param model sets the intermediate model.
     * @param project sets the current project, which is parsed.
     * @param dataTypeParser sets the DataTypeParser.
     */
    public JavaTypeExtractor(IntermediateModel model, IJavaProject project, DataTypeExtractor dataTypeParser) {
        this.dataTypeParser = dataTypeParser;
        this.model = model;
        this.project = project;
        methodParser = new JavaMethodExtractor(dataTypeParser);
    }

    /**
     * Parses a list of potential external types. If the model does not contain the type, and an IType can be found, it
     * will add an external ExtractedType to the model.
     * @param externalTypes is the set of external types to parse.
     * @throws JavaModelException if there are problem with the JDT API.
     */
    public void parseExternalTypes(Set<String> externalTypes) throws JavaModelException {
        logger.info("Parsing external types...");
        for (String typeName : externalTypes) { // for every potential external type
            if (!model.contains(typeName)) { // if is not a model type:
                IType iType = project.findType(typeName); // try to find IType
                if (iType != null) { // if IType was found:
                    model.addExternal(parseType(iType));  // add to model.
                }
            }
        }
    }

    /**
     * Parses {@link IType}. Detects whether the type is a (abstract) class, an interface or an enumeration.
     * @param iType is the {@link IType} to parse.
     * @return the extracted type.
     * @throws JavaModelException if there are problem with the JDT API.
     */
    public ExtractedType parseType(IType iType) throws JavaModelException {
        ExtractedType extractedType = null;
        if (iType.isClass()) {
            extractedType = parseClass(iType); // create class
        } else if (iType.isInterface()) {
            extractedType = parseInterface(iType);
        } else if (iType.isEnum()) {
            extractedType = parseEnumeration(iType); // create enum
        }
        parseOuterType(iType, extractedType); // parse outer type name
        dataTypeParser.parseTypeParameters(iType, extractedType);
        parseAttributes(iType, extractedType); // parse attribute
        methodParser.parseMethods(iType, extractedType); // parse methods
        for (String signature : iType.getSuperInterfaceTypeSignatures()) {
            extractedType.addInterface(dataTypeParser.parseDataType(signature, iType)); // add interface
        }
        return extractedType;
    }

    /**
     * Checks whether an {@link IType} inherits from the class {@link java.lang.Throwable}
     */
    private boolean inheritsFromThrowable(IType type) throws JavaModelException {
        ITypeHierarchy hierarchy = type.newSupertypeHierarchy(new NullProgressMonitor()); // get super type hierarchy
        for (IType superType : hierarchy.getAllSuperclasses(type)) { // for every super type
            if ("java.lang.Throwable".equals(superType)) { // if is called throwable
                return true; // is true
            }
        }
        return false; // is false
    }

    /**
     * Parses Attributes from an {@link IType} and adds them to an {@link ExtractedType}.
     */
    private void parseAttributes(IType iType, ExtractedType extractedType) throws JavaModelException {
        ExtractedAttribute attribute;
        for (IField field : iType.getFields()) {
            if (!isEnum(field)) { // if is no enumeral
                attribute = dataTypeParser.parseField(field, iType);
                attribute.setFinal(isFinal(field));
                attribute.setStatic(isStatic(field));
                attribute.setModifier(getModifier(field));
                extractedType.addAttribute(attribute);
            }
        }
    }

    /**
     * Parses an {@link IType} that has been identified as class.
     */
    private ExtractedClass parseClass(IType type) throws JavaModelException {
        boolean throwable = inheritsFromThrowable(type);
        ExtractedClass newClass = new ExtractedClass(getName(type), isAbstract(type), throwable);
        String signature = type.getSuperclassTypeSignature();
        if (signature != null) { // get full super type:
            newClass.setSuperClass(dataTypeParser.parseDataType(signature, type)); // set super
        }
        return newClass;
    }

    /**
     * Parse an {@link IType} that has been identified as enumeration.
     */
    private ExtractedEnumeration parseEnumeration(IType type) throws JavaModelException {
        ExtractedEnumeration newEnum = new ExtractedEnumeration(getName(type));
        for (IField field : type.getFields()) { // for every enumeral
            if (isEnum(field)) {
                newEnum.addEnumeral(new ExtractedEnumeral(field.getElementName())); // add to enum
            }
        }
        return newEnum;
    }

    /**
     * Parses an {@link IType} that has been identified as interface.
     */
    private ExtractedInterface parseInterface(IType type) throws JavaModelException {
        return new ExtractedInterface(getName(type)); // create interface
    }

    /**
     * Parses the outer type name of an {@link IType} if it has one.
     * @param iType is the {@link IType}.
     * @param extractedType is the {@link ExtractedType} which receives the parsed information.
     */
    private void parseOuterType(IType iType, ExtractedType extractedType) {
        IType outerType = iType.getDeclaringType();
        if (outerType != null) { // if is inner type
            extractedType.setOuterType(getName(outerType)); // add outer type name
        }
    }
}