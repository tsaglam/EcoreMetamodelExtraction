package eme.parser;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;

import eme.model.ExtractedClass;
import eme.model.ExtractedEnumeration;
import eme.model.ExtractedInterface;
import eme.model.ExtractedMethod;
import eme.model.ExtractedPackage;
import eme.model.ExtractedType;
import eme.model.IntermediateModel;
import eme.model.MethodType;
import eme.model.datatypes.AccessLevelModifier;
import eme.model.datatypes.ExtractedAttribute;

/**
 * The class analyzes java projects and builds intermediate models.
 * @author Timur Saglam
 */
public class JavaProjectParser {
    private static final Logger logger = LogManager.getLogger(JavaProjectParser.class.getName());
    private IntermediateModel currentModel;
    private ExtractedPackage currentPackage;
    private IJavaProject currentProject;
    private int packageCounter;
    private DataTypeParser typeParser;

    /**
     * Analyzes a java project and builds an intermediate model.
     * @param project is the Java project to analyze.
     * @return an intermediate model that was extracted from the project.
     */
    public IntermediateModel buildIntermediateModel(IJavaProject project) {
        logger.info("Started parsing the project...");
        currentProject = project; // set project
        typeParser = new DataTypeParser();
        currentModel = new IntermediateModel(project.getElementName()); // create new model.
        try {
            parsePackages(project); // parse project
            parseExternalTypes(typeParser.getPotentialExternalTypes()); // parse potential external types.
        } catch (JavaModelException exception) {
            logger.fatal("Error while extracting the model.", exception);
        }
        currentModel.sort(); // sort model content
        currentModel.print(); // print intermediate model.
        return currentModel;
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
     * Checks if a packet fragment is a source package.
     */
    private boolean isSourcePackage(IPackageFragment packageFragment) throws JavaModelException {
        return packageFragment.getKind() == IPackageFragmentRoot.K_SOURCE;
    }

    /**
     * Parses Attributes from an IType and adds them to an ExtractedType.
     */
    private void parseAttributes(IType iType, ExtractedType extractedType) throws JavaModelException {
        ExtractedAttribute attribute;
        for (IField field : iType.getFields()) {
            int flags = field.getFlags();
            if (!Flags.isEnum(flags)) { // if is no enumeral
                attribute = typeParser.parseField(field, iType);
                attribute.setFlags(AccessLevelModifier.getFrom(flags), Flags.isStatic(flags), Flags.isFinal(flags));
                extractedType.addAttribute(attribute);
            }
        }
    }

    /**
     * Parses an IType that has been identified as class.
     */
    private ExtractedClass parseClass(IType type) throws JavaModelException {
        boolean isAbstract = Flags.isAbstract(type.getFlags());
        ExtractedClass newClass = new ExtractedClass(type.getFullyQualifiedName(), isAbstract);
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
     * Extracts all compilation units from a list of package fragments. It then parses all ICompilationUnits while
     * updating the current package.
     */
    private void parseCompilationUnits(List<IPackageFragment> fragments) throws JavaModelException {
        for (IPackageFragment fragment : fragments) { // for every package fragment
            currentPackage = currentModel.getPackage(fragment.getElementName()); // model package
            reportProgress(fragments.size());
            for (ICompilationUnit unit : fragment.getCompilationUnits()) { // get compilation units
                for (IType iType : unit.getAllTypes()) { // for all types
                    currentModel.addTo(parseType(iType), currentPackage);
                }
            }
        }
    }

    /**
     * Parse an IType that has been identified as enumeration.
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

    /**
     * Parses a list of potential external types. If the model does not contain the type, and an IType can be found, it
     * will add an external ExtractedType to the model.
     */
    private void parseExternalTypes(Set<String> externalTypes) throws JavaModelException {
        logger.info("Parsing external types...");
        for (String typeName : externalTypes) { // for every potential external type
            if (!currentModel.contains(typeName)) { // if is not a model type:
                IType iType = currentProject.findType(typeName); // try to find IType
                if (iType != null) { // if IType was found:
                    currentModel.addExternal(parseType(iType));  // add to model.
                    logger.info("Resolved external type " + typeName);
                }
            }
        }
    }

    /**
     * Parses the methods from an IType and adds them to an ExtractedType.
     */
    private void parseMethods(IType iType, ExtractedType extractedType) throws JavaModelException {
        ExtractedMethod extractedMethod;
        String methodName; // name of the extracted method
        for (IMethod method : iType.getMethods()) { // for every method
            methodName = iType.getFullyQualifiedName() + "." + method.getElementName(); // build name
            int flags = method.getFlags();
            extractedMethod = new ExtractedMethod(methodName, typeParser.parseReturnType(method));
            extractedMethod.setFlags(AccessLevelModifier.getFrom(flags), parseMethodType(method), Flags.isStatic(flags), Flags.isAbstract(flags));
            for (ILocalVariable parameter : method.getParameters()) { // parse parameters:
                extractedMethod.addParameter(typeParser.parseParameter(parameter, method));
            }
            for (String exception : method.getExceptionTypes()) { // parse throw declarations:
                extractedMethod.addThrowsDeclaration(typeParser.parseDataType(exception, iType));
            }
            extractedType.addMethod(extractedMethod);
        }
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
        }
        return MethodType.METHOD;
    }

    /**
     * The method takes an IJavaProject and extracts the package structure of the project. It continues by parsing the
     * IPackageFragments. The method creates the packages from a set of package names to avoid the problem of duplicate
     * default packages. But all other parsing calls are done with a list of fragments.
     */
    private void parsePackages(IJavaProject project) throws JavaModelException {
        SortedSet<String> packageNames = new TreeSet<String>(); // set to avoid duplicates
        List<IPackageFragment> fragments = new LinkedList<IPackageFragment>();
        for (IPackageFragment fragment : project.getPackageFragments()) {
            if (isSourcePackage(fragment)) { // only source packages, no binary packages.
                fragments.add(fragment); // reuse fragments for class extraction
                packageNames.add(fragment.getElementName()); // add name to set.
            }
        }
        for (String name : packageNames) {
            currentModel.add(new ExtractedPackage(name)); // build model packages first
        }
        parseCompilationUnits(fragments); // then continue parsing
    }

    /**
     * Parses IType. Detects whether the type is a (abstract) class, an interface or an enumeration.
     */
    private ExtractedType parseType(IType iType) throws JavaModelException {
        ExtractedType extractedType = null;
        if (iType.isClass()) {
            extractedType = parseClass(iType); // create class
        } else if (iType.isInterface()) {
            extractedType = new ExtractedInterface(iType.getFullyQualifiedName()); // create interface
        } else if (iType.isEnum()) {
            extractedType = parseEnumeration(iType); // create enum
        }
        typeParser.parseTypeParameters(iType, extractedType);
        parseAttributes(iType, extractedType); // parse attributes
        parseMethods(iType, extractedType); // parse methods
        for (IType superInterface : iType.newSupertypeHierarchy(null).getSuperInterfaces(iType)) {
            extractedType.addInterface(superInterface.getFullyQualifiedName()); // add interface
        }
        return extractedType;
    }

    /**
     * Reports on the parsing progress by logging the current package.
     */
    private void reportProgress(int packages) {
        packageCounter++; // increase package count
        logger.info("Parsing package " + currentPackage.getFullName() + " (" + packageCounter + "/" + packages + ")");
        packageCounter = (packageCounter == packages) ? 0 : packageCounter; // reset to zero if finished
    }
}