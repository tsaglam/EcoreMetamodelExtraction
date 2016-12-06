package eme;

import org.eclipse.core.resources.IProject;

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
        analyzer.analyze(project);
        // TODO get model from analyzer
        // TODO save model to existing project or new project
    }
}
