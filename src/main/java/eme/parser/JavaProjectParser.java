package eme.parser;

import java.util.LinkedList;
import java.util.List;
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

import eme.model.ExtractedClass;
import eme.model.ExtractedEnumeration;
import eme.model.ExtractedInterface;
import eme.model.ExtractedMethod;
import eme.model.ExtractedPackage;
import eme.model.ExtractedType;
import eme.model.IntermediateModel;
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

    /**
     * Analyzes a java project and builds an intermediate model.
     * @param project is the Java project to analyze.
     * @return an intermediate model that was extracted from the project.
     */
    public IntermediateModel buildIntermediateModel(IJavaProject project) {
        logger.info("Started parsing the project");
        currentModel = new IntermediateModel(project.getElementName()); // create new model.
        try {
            parsePackages(project); // parse project
        } catch (JavaModelException exception) {
            logger.fatal("Error while extracting the model.", exception);
        }
        currentModel.print(); // print intermediate model.
        return currentModel;
    }

    /**
     * Checks if a packet fragment is a source package.
     */
    private boolean isSourcePackage(IPackageFragment packageFragment) throws JavaModelException {
        return packageFragment.getKind() == IPackageFragmentRoot.K_SOURCE;
    }

    /**
     * Parses Attributes from an IType and adds them to an ExtractedType.
     * @param iType is the IType.
     * @param extractedType is the ExtractedType.
     */
    private void parseAttributes(IType iType, ExtractedType extractedType) throws JavaModelException {
        ExtractedAttribute attribute;
        for (IField field : iType.getFields()) {
            attribute = DataTypeParser.parseField(field, iType);
            int flags = field.getFlags();
            attribute.setFlags(AccessLevelModifier.getFrom(flags), Flags.isStatic(flags), Flags.isFinal(flags));
            extractedType.addAttribute(attribute);
        }
    }

    /**
     * Parses an IType that has been identified as class.
     * @param type is the IType.
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
        currentModel.addTo(newClass, currentPackage);
        return newClass;
    }

    /**
     * Extracts all compilation units from a list of package fragments. It then parses all ICompilationUnits while
     * updating the current package.
     * @param fragments is the list of compilation units.
     */
    private void parseCompilationUnits(List<IPackageFragment> fragments) throws JavaModelException {
        for (IPackageFragment fragment : fragments) { // for every package fragment
            currentPackage = currentModel.getPackage(fragment.getElementName()); // model package
            for (ICompilationUnit unit : fragment.getCompilationUnits()) { // get compilation units
                parseTypes(unit); // extract classes
            }
        }
    }

    /**
     * Parse an IType that has been identified as enumeration.
     * @param type is the IType.
     */
    private ExtractedEnumeration parseEnumeration(IType type) throws JavaModelException {
        ExtractedEnumeration newEnum = new ExtractedEnumeration(type.getFullyQualifiedName());
        for (IField field : type.getFields()) { // for every enumeral
            if (Flags.isEnum(field.getFlags())) {
                newEnum.addEnumeral(field.getElementName()); // add to enum
            }
        }
        currentModel.addTo(newEnum, currentPackage);
        return newEnum;
    }

    /**
     * Parses an IType that has been identified as interface.
     * @param type is the IType.
     */
    private ExtractedInterface parseInterface(IType type) throws JavaModelException {
        ExtractedInterface newInterface = new ExtractedInterface(type.getFullyQualifiedName());
        currentModel.addTo(newInterface, currentPackage);
        return newInterface;
    }

    /**
     * Parses the methods from an IType and adds them to an ExtractedType.
     * @param iType is the IType where the methods are from.
     * @param extractedType is the ExtractedType where the methods are getting added.
     */
    private void parseMethods(IType iType, ExtractedType extractedType) throws JavaModelException {
        ExtractedMethod extractedMethod;
        String methodName; // name of the extracted method
        for (IMethod method : iType.getMethods()) { // for every method
            methodName = iType.getFullyQualifiedName() + "." + method.getElementName(); // name
            int flags = method.getFlags();
            extractedMethod = new ExtractedMethod(methodName, DataTypeParser.parseReturnType(method), method.isConstructor());
            extractedMethod.setFlags(AccessLevelModifier.getFrom(flags), Flags.isStatic(flags), Flags.isAbstract(flags));
            for (ILocalVariable parameter : method.getParameters()) {
                extractedMethod.addParameter(DataTypeParser.parseParameter(parameter, method));
            }
            extractedType.addMethod(extractedMethod);
        }
    }

    /**
     * The method takes an IJavaProject and extracts the package structure of the project. It continues by parsing the
     * IPackageFragments. The method creates the packages from a set of package names to avoid the problem of duplicate
     * default packages. But all other parsing calls are done with a list of fragments.
     * @param project is the IJavaProject that gets parsed.
     */
    private void parsePackages(IJavaProject project) throws JavaModelException {
        SortedSet<String> packageNames = new TreeSet<String>(); // set to avoid duplicates
        List<IPackageFragment> extractedFragments = new LinkedList<IPackageFragment>();
        for (IPackageFragment fragment : project.getPackageFragments()) {
            if (isSourcePackage(fragment)) { // only source packages, no binary packages.
                extractedFragments.add(fragment); // reuse fragments for class extraction
                packageNames.add(fragment.getElementName()); // add name to set.
            }
        }
        for (String name : packageNames) {
            currentModel.add(new ExtractedPackage(name)); // build model packages first
        }
        parseCompilationUnits(extractedFragments); // then continue parsing
    }

    /**
     * Parses ICompilationUnit. Detects (abstract) classes, interfaces and enumerations.
     * @param compilationUnit is the given ICompilationUnit.
     */
    private void parseTypes(ICompilationUnit compilationUnit) throws JavaModelException {
        ExtractedType extractedType = null;
        for (IType iType : compilationUnit.getAllTypes()) { // for all types
            if (iType.isClass()) {
                extractedType = parseClass(iType); // create class
            } else if (iType.isInterface()) {
                extractedType = parseInterface(iType); // create interface
            } else if (iType.isEnum()) {
                extractedType = parseEnumeration(iType); // create enum
            }
            parseMethods(iType, extractedType); // parse methods
            parseAttributes(iType, extractedType); // parse attributes
            for (IType superInterface : iType.newSupertypeHierarchy(null).getSuperInterfaces(iType)) {
                extractedType.addInterface(superInterface.getFullyQualifiedName()); // add interface
            }
        }
    }
}