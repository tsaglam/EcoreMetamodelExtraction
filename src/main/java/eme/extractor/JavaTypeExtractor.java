package eme.extractor;

import static eme.extractor.JDTUtil.getName;
import static eme.extractor.JDTUtil.isAbstract;
import static eme.extractor.JDTUtil.isEnum;

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
import eme.model.ExtractedEnum;
import eme.model.ExtractedEnumConstant;
import eme.model.ExtractedInterface;
import eme.model.ExtractedType;
import eme.model.IntermediateModel;

/**
 * Extractor class for Java types (classes, interfaces, enumerations). This class uses the {@link JavaMemberExtractor}
 * and the {@link DataTypeExtractor}.
 * @author Timur Saglam
 */
public class JavaTypeExtractor {
    private static final Logger logger = LogManager.getLogger(JavaTypeExtractor.class.getName());
    private final DataTypeExtractor dataTypeExtractor;
    private final JavaMemberExtractor memberExtractor;
    private final IntermediateModel model;
    private final IJavaProject project;

    /**
     * Basic constructor.
     * @param model sets the intermediate model.
     * @param project sets the current project, which is extracted.
     * @param dataTypeExtractor sets the DataTypeParser.
     */
    public JavaTypeExtractor(IntermediateModel model, IJavaProject project, DataTypeExtractor dataTypeExtractor) {
        this.dataTypeExtractor = dataTypeExtractor;
        this.model = model;
        this.project = project;
        memberExtractor = new JavaMemberExtractor(dataTypeExtractor);
    }

    /**
     * Parses a list of potential external types. If the model does not contain the type, and an IType can be found, it
     * will add an external ExtractedType to the model.
     * @param externalTypes is the set of external types to extract.
     * @throws JavaModelException if there are problem with the JDT API.
     */
    public void extractExternalTypes(Set<String> externalTypes) throws JavaModelException {
        logger.info("Parsing external types...");
        for (String typeName : externalTypes) { // for every potential external type
            if (!model.contains(typeName)) { // if is not a model type:
                IType type = project.findType(typeName); // try to find IType
                if (type != null) { // if IType was found:
                    ExtractedType extractedType = extractType(type);
                    logger.info("Resolved external " + extractedType.toString());
                    model.addExternal(extractedType);  // add to model.
                }
            }
        }
    }

    /**
     * Parses {@link IType}. Detects whether the type is a (abstract) class, an interface or an enumeration.
     * @param type is the {@link IType} to extract.
     * @return the extracted type.
     * @throws JavaModelException if there are problem with the JDT API.
     */
    public ExtractedType extractType(IType type) throws JavaModelException {
        ExtractedType extractedType = null;
        if (type.isClass()) {
            extractedType = extractClass(type); // create class
        } else if (type.isInterface()) {
            extractedType = extractInterface(type);
        } else if (type.isEnum()) {
            extractedType = extractEnum(type); // create enum
        }
        extractOuterType(type, extractedType); // extract outer type name
        extractedType.setTypeParameters(dataTypeExtractor.extractTypeParameters(type.getTypeParameters(), type));
        memberExtractor.extractFields(type, extractedType); // extract attribute
        memberExtractor.extractMethods(type, extractedType); // extract methods
        for (String signature : type.getSuperInterfaceTypeSignatures()) {
            extractedType.addInterface(dataTypeExtractor.extractDataType(signature, type)); // add interface
        }
        return extractedType;
    }

    /**
     * Parses an {@link IType} that has been identified as class.
     */
    private ExtractedClass extractClass(IType type) throws JavaModelException {
        boolean throwable = inheritsFromThrowable(type);
        ExtractedClass newClass = new ExtractedClass(getName(type), isAbstract(type), throwable);
        String signature = type.getSuperclassTypeSignature();
        if (signature != null) { // get full super type:
            newClass.setSuperClass(dataTypeExtractor.extractDataType(signature, type)); // set super
        }
        return newClass;
    }

    /**
     * Parse an {@link IType} that has been identified as enumeration.
     */
    private ExtractedEnum extractEnum(IType type) throws JavaModelException {
        ExtractedEnum newEnum = new ExtractedEnum(getName(type));
        for (IField field : type.getFields()) { // for every enumeral
            if (isEnum(field)) {
                newEnum.addConstant(new ExtractedEnumConstant(field.getElementName())); // add to enum
            }
        }
        return newEnum;
    }

    /**
     * Parses an {@link IType} that has been identified as interface.
     */
    private ExtractedInterface extractInterface(IType type) throws JavaModelException {
        return new ExtractedInterface(getName(type)); // create interface
    }

    /**
     * Parses the outer type name of an {@link IType} if it has one.
     * @param type is the {@link IType}.
     * @param extractedType is the {@link ExtractedType} which receives the extracted information.
     */
    private void extractOuterType(IType type, ExtractedType extractedType) {
        IType outerType = type.getDeclaringType();
        if (outerType != null) { // if is inner type
            extractedType.setOuterType(getName(outerType)); // add outer type name
        }
    }

    /**
     * Checks whether an {@link IType} inherits from the class {@link java.lang.Throwable}
     */
    private boolean inheritsFromThrowable(IType type) throws JavaModelException {
        ITypeHierarchy hierarchy = type.newSupertypeHierarchy(new NullProgressMonitor()); // get super type hierarchy
        for (IType superType : hierarchy.getAllSuperclasses(type)) { // for every super type
            if ("java.lang.Throwable".equals(superType.getFullyQualifiedName())) { // if is called throwable
                return true; // is true
            }
        }
        return false; // is false
    }
}