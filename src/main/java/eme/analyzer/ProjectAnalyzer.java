package eme.analyzer;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;

/**
 * The class analyzes java projects and builds intermediate models.
 * @author Timur Saglam
 */
public class ProjectAnalyzer {

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
        try {
            extractPackageStructure(project);
        } catch (JavaModelException exception) {
            System.out.println("Error while extracting the package structure: " + exception.getMessage());
        }
        // TODO implement analyze(IProject project)
    }

    /**
     * TODO COMMENT
     * @param project
     * @throws JavaModelException
     */
    public void extractPackageStructure(IJavaProject project) throws JavaModelException {
        IPackageFragment[] packages = project.getPackageFragments();
        for (IPackageFragment packageFragment : packages) {
            if (isSourcePackage(packageFragment)) { // only source packages, no binary packages.
                System.out.println(packageFragment.getElementName()); // TODO CONTINUE HERE
            }
        }
    }

    /**
     * Checks if a packet fragment is a source package.
     */
    private boolean isSourcePackage(IPackageFragment packageFragment) throws JavaModelException {
        return packageFragment.getKind() == IPackageFragmentRoot.K_SOURCE;
    }
}
