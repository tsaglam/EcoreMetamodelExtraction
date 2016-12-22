package eme.parser;

import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;

import eme.model.ExtractedClass;
import eme.model.ExtractedEnumeration;
import eme.model.ExtractedInterface;
import eme.model.ExtractedPackage;
import eme.model.ExtractedType;
import eme.model.IntermediateModel;

/**
 * The class analyzes java projects and builds intermediate models.
 * @author Timur Saglam
 */
public class JavaProjectParser {
    private IntermediateModel currentModel;
    private ExtractedPackage currentPackage;
    private final boolean printModel;

    /**
     * Basic constructor.
     */
    public JavaProjectParser() {
        printModel = true;
    }

    /**
     * Analyzes a java project and builds an intermediate model.
     * @param project is the Java project to analyze.
     * @return an intermediate model that was extracted from the project.
     */
    public IntermediateModel buildIntermediateModel(IJavaProject project) {
        currentModel = new IntermediateModel(project.getElementName()); // create new model.
        try {
            parseIJavaProject(project); // parse project
        } catch (JavaModelException exception) {
            System.out.println("Error while extracting the model: " + exception.getMessage());
        }
        if (printModel) { // if printing is enabled TODO (MEDIUM) check in model class
            currentModel.print(); // print intermediate model.
        }
        return currentModel;
    } // TODO (MEDIUM) improve code style, maybe split class etc.

    /**
     * Checks if a packet fragment is a source package.
     */
    private boolean isSourcePackage(IPackageFragment packageFragment) throws JavaModelException {
        return packageFragment.getKind() == IPackageFragmentRoot.K_SOURCE;
    }

    /**
     * Parses an IType that has been identified as class.
     * @param type is the IType.
     */
    private ExtractedClass parseClass(IType type) throws JavaModelException {
        boolean isAbstract = Flags.isAbstract(type.getFlags());
        ExtractedClass newClass = new ExtractedClass(type.getFullyQualifiedName(), isAbstract);
        newClass.setSuperClass(type.getSuperclassName());
        if (type.getSuperclassName() != null) { // get full supertype name:
            newClass.setSuperClass(type.newSupertypeHierarchy(null).getSuperclass(type).getFullyQualifiedName());
        }
        currentModel.addTo(newClass, currentPackage);
        return newClass;
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
     * Parses ICompilationUnit. Detects (abstract) classes, interfaces and enumerations.
     * @param compilationUnit is the given ICompilationUnit.
     */
    private void parseICompilationUnit(ICompilationUnit compilationUnit) throws JavaModelException {
        ExtractedType newType = null;
        for (IType type : compilationUnit.getAllTypes()) { // for all types
            if (type.isClass()) {
                newType = parseClass(type); // create class
            } else if (type.isInterface()) {
                newType = parseInterface(type); // create interface
            } else if (type.isEnum()) {
                newType = parseEnumeration(type); // create enum
            }
            for (IType superInterface : type.newSupertypeHierarchy(null).getSuperInterfaces(type)) {
                newType.addSuperInterface(superInterface.getFullyQualifiedName()); // add interface
            }
        }
    }

    /**
     * The method takes an IJavaProject and extracts the package structure of the project. It
     * continues by parsing the IPackageFragments. The method creates the packages from a set of
     * package names to avoid the problem of duplicate default packages. But all other parsing calls
     * are done with a list of fragments.
     * @param project is the IJavaProject that gets parsed.
     */
    private void parseIJavaProject(IJavaProject project) throws JavaModelException {
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
        parseIPackageFragments(extractedFragments); // then continue parsing
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
     * Extracts all compilation units from a list of package fragments. It then parses all
     * ICompilationUnits while updating the current package.
     * @param fragments is the list of compilation units.
     */
    private void parseIPackageFragments(List<IPackageFragment> fragments) throws JavaModelException {
        for (IPackageFragment fragment : fragments) { // for every package fragment
            currentPackage = currentModel.getPackage(fragment.getElementName()); // model package
            for (ICompilationUnit unit : fragment.getCompilationUnits()) { // get compilation units
                parseICompilationUnit(unit); // extract classes
            }
        }
    }
}