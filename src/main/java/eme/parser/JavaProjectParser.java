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
import org.eclipse.jdt.core.JavaModelException;

import eme.model.ExtractedClass;
import eme.model.ExtractedEnumeration;
import eme.model.ExtractedInterface;
import eme.model.ExtractedPackage;
import eme.model.IntermediateModel;

/**
 * The class analyzes java projects and builds intermediate models.
 * @author Timur Saglam
 */
public class JavaProjectParser {
    private IntermediateModel currentModel;
    private boolean printExtractedModel;

    /**
     * Basic constructor.
     */
    public JavaProjectParser() {
        printExtractedModel = true;
    }

    /**
     * Analyzes a java project and builds an intermediate model.
     * @param project is the Java project to analyze.
     * @return an intermediate model that was extracted from the project.
     */
    public IntermediateModel buildModel(IJavaProject project) {
        currentModel = new IntermediateModel(project.getElementName()); // create new model.
        try {
            parseIJavaProject(project); // TODO (HIGH) use recursive functions for this call
        } catch (JavaModelException exception) {
            System.out.println("Error while extracting the model: " + exception.getMessage());
        }
        if (printExtractedModel) {
            currentModel.print();
        }
        return currentModel;
    }

    /**
     * Returns the full qualified name of an IType.
     * @param type is the IType.
     * @return the full qualified name.
     */
    private String getName(IType type) {
        return type.getFullyQualifiedName(); // just to make the call shorter
    }

    /**
     * Checks if a packet fragment is a source package.
     */
    private boolean isSourcePackage(IPackageFragment packageFragment) throws JavaModelException {
        return packageFragment.getKind() == IPackageFragmentRoot.K_SOURCE;
    }

    /**
     * Parses an IType that has been identified as class.
     * @param type is the IType.
     * @return the ExtractedClass.
     */
    private ExtractedClass parseClass(IType type) throws JavaModelException {
        boolean isAbstract = Flags.isAbstract(type.getFlags());
        ExtractedClass extractedClass = new ExtractedClass(getName(type), isAbstract);
        currentModel.add(extractedClass);
        return extractedClass;
    }

    /**
     * Parse an IType that has been identified as enumeration.
     * @param type is the IType.
     * @return the ExtractedInterface.
     */
    private ExtractedEnumeration parseEnumeration(IType type) throws JavaModelException {
        ExtractedEnumeration enumeration = new ExtractedEnumeration(getName(type)); // new enum
        for (IField field : type.getFields()) { // for every enumeral
            enumeration.addEnumeral(field.getElementName()); // add to enum
        }
        currentModel.add(enumeration);
        return enumeration;
    }

    /**
     * Parses ICompilationUnit. Detects (abstract) classes, interfaces and enumerations.
     * @param compilationUnit is the given ICompilationUnit.
     */
    private void parseICompilationUnit(ICompilationUnit compilationUnit) throws JavaModelException {
        for (IType type : compilationUnit.getAllTypes()) { // for all types
            if (type.isClass()) {
                parseClass(type);
            } else if (type.isInterface()) {
                parseInterface(type);
            } else if (type.isEnum()) {
                parseEnumeration(type);
            }
        }
    }

    /**
     * The method takes an IJavaProject and extracts the package structure of the project. It
     * continues by parsing the IPackageFragments.
     * @param project is the IJavaProject that gets parsed.
     */
    private void parseIJavaProject(IJavaProject project) throws JavaModelException {
        SortedSet<String> packageNames = new TreeSet<String>(); // set to avoid duplicates
        List<IPackageFragment> extractedFragments = new LinkedList<IPackageFragment>();
        for (IPackageFragment fragment : project.getPackageFragments()) {
            if (isSourcePackage(fragment)) { // only source packages, no binary packages.
                extractedFragments.add(fragment); // reuse fragments for class extraction
                packageNames.add(fragment.getElementName()); // add to list.
            }
        }
        for (String name : packageNames) {
            currentModel.add(new ExtractedPackage(name)); // build model packages
        }
        parseIPackageFragments(extractedFragments); // continue parsing
    }

    /**
     * Parses an IType that has been identified as interface.
     * @param type is the IType.
     * @return the ExtractedInterface.
     */
    private ExtractedInterface parseInterface(IType type) throws JavaModelException {
        ExtractedInterface extractedInterface = new ExtractedInterface(getName(type));
        currentModel.add(extractedInterface);
        return extractedInterface;
    }

    /**
     * Extracts all compilation units from a list of package fragments. It then parses all
     * ICompilationUnits.
     * @param extractedFragments is the list of compilation units.
     */
    private void parseIPackageFragments(List<IPackageFragment> extractedFragments) throws JavaModelException {
        for (IPackageFragment fragment : extractedFragments) { // for every package fragment
            for (ICompilationUnit unit : fragment.getCompilationUnits()) { // get compilation units
                parseICompilationUnit(unit); // extract classes
            }
        }
    }
}
