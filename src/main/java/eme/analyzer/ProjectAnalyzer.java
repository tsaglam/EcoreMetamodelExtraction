package eme.analyzer;

import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;

import eme.model.ExtractedPackage;
import eme.model.IntermediateModel;

/**
 * The class analyzes java projects and builds intermediate models.
 * @author Timur Saglam
 */
public class ProjectAnalyzer {

    private IntermediateModel currentModel;

    /**
     * Basic constructor.
     */
    public ProjectAnalyzer() {
    }

    /**
     * Analyzes a java project and builds an intermediate model.
     * @param project is the Java project to analyze.
     */
    public void analyze(IJavaProject project) {
        currentModel = new IntermediateModel(); // create new model.
        try {
            extractPackageStructure(project);
        } catch (JavaModelException exception) {
            System.out.println("Error while extracting the package structure: " + exception.getMessage());
        }
        // TODO implement analyze(IProject project)
        currentModel.print();
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
