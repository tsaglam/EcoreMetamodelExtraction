package eme.parser;

import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;

import eme.model.ExtractedClass;
import eme.model.ExtractedEnumeration;
import eme.model.ExtractedInterface;
import eme.model.ExtractedType;
import eme.model.IntermediateModel;
import eme.model.datatypes.AccessLevelModifier;
import eme.model.datatypes.ExtractedAttribute;

/**
 * Parser class for Java types (classes, interfaces, enumerations). The class uses the {@link JavaMethodParser} and the
 * {@link DataTypeParser}.
 * @author Timur Saglam
 */
public class JavaTypeParser {
    private static final Logger logger = LogManager.getLogger(JavaTypeParser.class.getName());
    private final DataTypeParser dataTypeParser;
    private final JavaMethodParser methodParser;
    private final IntermediateModel model;
    private final IJavaProject project;

    /**
     * Basic constructor.
     * @param model sets the intermediate model.
     * @param project sets the current project, which is parsed.
     * @param dataTypeParser sets the DataTypeParser.
     */
    public JavaTypeParser(IntermediateModel model, IJavaProject project, DataTypeParser dataTypeParser) {
        this.dataTypeParser = dataTypeParser;
        this.model = model;
        this.project = project;
        methodParser = new JavaMethodParser(dataTypeParser);
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
                    logger.info("Resolved external type " + typeName);
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
            extractedType = new ExtractedInterface(iType.getFullyQualifiedName()); // create interface
        } else if (iType.isEnum()) {
            extractedType = parseEnumeration(iType); // create enum
        }
        dataTypeParser.parseTypeParameters(iType, extractedType);
        parseAttributes(iType, extractedType); // parse attributes
        methodParser.parseMethods(iType, extractedType); // parse methods
        for (IType superInterface : iType.newSupertypeHierarchy(null).getSuperInterfaces(iType)) {
            extractedType.addInterface(superInterface.getFullyQualifiedName()); // add interface
        }
        return extractedType;
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

    /**
     * Parses Attributes from an {@link IType} and adds them to an {@link ExtractedType}.
     */
    private void parseAttributes(IType iType, ExtractedType extractedType) throws JavaModelException {
        ExtractedAttribute attribute;
        for (IField field : iType.getFields()) {
            int flags = field.getFlags();
            if (!Flags.isEnum(flags)) { // if is no enumeral
                attribute = dataTypeParser.parseField(field, iType);
                attribute.setFlags(AccessLevelModifier.getFrom(flags), Flags.isStatic(flags), Flags.isFinal(flags));
                extractedType.addAttribute(attribute);
            }
        }
    }

    /**
     * Parses an {@link IType} that has been identified as class.
     */
    private ExtractedClass parseClass(IType type) throws JavaModelException {
        boolean isAbstract = Flags.isAbstract(type.getFlags());
        boolean throwable = inheritsFromThrowable(type);
        ExtractedClass newClass = new ExtractedClass(type.getFullyQualifiedName(), isAbstract, throwable);
        newClass.setSuperClass(type.getSuperclassName());
        if (type.getSuperclassName() != null) { // get full super type:
            IType superType = type.newSupertypeHierarchy(null).getSuperclass(type);
            if (superType != null) { // could be null, prevents exception
                newClass.setSuperClass(superType.getFullyQualifiedName()); // set super type name.
            }
        }
        return newClass;
    }

    /**
     * Parse an {@link IType} that has been identified as enumeration.
     */
    private ExtractedEnumeration parseEnumeration(IType type) throws JavaModelException {
        ExtractedEnumeration newEnum = new ExtractedEnumeration(type.getFullyQualifiedName());
        for (IField field : type.getFields()) { // for every enumeral
            if (Flags.isEnum(field.getFlags())) {
                newEnum.addEnumeral(field.getElementName()); // add to enum
            }
        }
        return newEnum;
    }
}