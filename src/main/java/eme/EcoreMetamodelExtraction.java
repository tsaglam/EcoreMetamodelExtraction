package eme;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.JavaCore;

import eme.analyzer.ProjectAnalyzer;

/**
 * Base class of the prototype for Ecore metamodel extraction.
 * @author Timur Saglam
 */
public class EcoreMetamodelExtraction {
    private ProjectAnalyzer analyzer;

    /**
     * Basic constructor.
     */
    public EcoreMetamodelExtraction() {
        analyzer = new ProjectAnalyzer();
    }

    /**
     * Starts the Ecore metamodel extraction for a specific project.
     * @param project is the specific project for the extraction.
     */
    public void extractFrom(IProject project) {
        check(project); // check if valid.
        analyzer.analyze(JavaCore.create(project));
        // TODO get model from analyzer
        // TODO save model to existing project or new project
    }

    /**
     * Checks whether a specific project is valid (neither null nor nonexistent)
     * @param project is the specific IJavaProject.
     */
    private void check(IProject project) {
        if (project == null) {
            throw new IllegalArgumentException("Project can't be null!");
        } else if (!project.exists()) {
            throw new IllegalArgumentException("Project " + project.toString() + "does not exist!");
        }
    }
}
