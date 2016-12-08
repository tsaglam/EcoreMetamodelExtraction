package eme.analyzer;

import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import eme.model.ExtractedClass;
import eme.model.ExtractedPackage;
import eme.model.IntermediateModel;

/**
 * The class analyzes java projects and builds intermediate models.
 * @author Timur Saglam
 */
public class ProjectAnalyzer {

    private IntermediateModel currentModel;
    private boolean printExtractedModel;

    /**
     * Basic constructor.
     */
    public ProjectAnalyzer() {
        printExtractedModel = true;
    }

    /**
     * Analyzes a java project and builds an intermediate model.
     * @param project is the Java project to analyze.
     * @return an intermediate model that was extracted from the project.
     */
    public IntermediateModel analyze(IJavaProject project) {
        currentModel = new IntermediateModel(project.getElementName()); // create new model.
        try {
            extractPackageStructure(project);
            extractClasses(project);
        } catch (JavaModelException exception) {
            System.out.println("Error while extracting the model: " + exception.getMessage());
        }
        if (printExtractedModel) {
            currentModel.print();
        }
        return currentModel;
    }

    public void extractClasses(IJavaProject project) throws JavaModelException {
        for (IPackageFragment fragment : project.getPackageFragments()) {
            if (isSourcePackage(fragment)) { // only source packages, no binary packages.
                for (ICompilationUnit unit : fragment.getCompilationUnits()) {
                    for (IType type : unit.getAllTypes()) {
                        currentModel.add(new ExtractedClass(type.getFullyQualifiedName()));
                    }
                }
            }
        }
    }

    /**
     * The method takes an IJavaProject and extracts the package structure of the project. It then
     * builds an intermediate model for the extracted structure.
     * @param project
     * @throws JavaModelException if there are problems with the package fragments of the project.
     */
    public void extractPackageStructure(IJavaProject project) throws JavaModelException {
        SortedSet<String> packageNames = new TreeSet<String>(); // set to avoid duplicates
        for (IPackageFragment fragment : project.getPackageFragments()) {
            if (isSourcePackage(fragment)) { // only source packages, no binary packages.
                packageNames.add(fragment.getElementName()); // add to list.
            }
        }
        for (String name : packageNames) {
            currentModel.add(new ExtractedPackage(name));
        }
    }

    /**
     * Checks if a packet fragment is a source package.
     */
    private boolean isSourcePackage(IPackageFragment packageFragment) throws JavaModelException {
        return packageFragment.getKind() == IPackageFragmentRoot.K_SOURCE;
    }
}
