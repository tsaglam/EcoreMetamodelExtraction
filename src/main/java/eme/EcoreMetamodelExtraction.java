package eme;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

import eme.analyzer.ProjectAnalyzer;
import eme.generator.EcoreMetamodelGenerator;
import eme.model.IntermediateModel;

/**
 * Base class of the prototype for Ecore metamodel extraction.
 * @author Timur Saglam
 */
public class EcoreMetamodelExtraction {
    private ProjectAnalyzer analyzer;
    private EcoreMetamodelGenerator generator;

    /**
     * Basic constructor.
     */
    public EcoreMetamodelExtraction() {
        analyzer = new ProjectAnalyzer();
        generator = new EcoreMetamodelGenerator();
    }

    /**
     * Starts the Ecore metamodel extraction for a specific project.
     * @param project is the specific project for the extraction.
     */
    public void extractFrom(IProject project) {
        check(project); // check if valid.
        IJavaProject javaProject = JavaCore.create(project); // create java project
        IntermediateModel model = analyzer.analyze(javaProject);
        generator.generateFrom(model);
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
