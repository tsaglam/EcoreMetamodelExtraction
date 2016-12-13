package eme;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

import eme.generator.EcoreMetamodelGenerator;
import eme.model.IntermediateModel;
import eme.parser.JavaProjectParser;

/**
 * Base class of the prototype for Ecore metamodel extraction.
 * @author Timur Saglam
 */
public class EcoreMetamodelExtraction {
    private ExtractionProperties properties;
    private JavaProjectParser parser;
    private EcoreMetamodelGenerator generator;
    
    /**
     * Basic constructor. Builds parser and generator.
     */
    public EcoreMetamodelExtraction() {
        properties = new ExtractionProperties();
        parser = new JavaProjectParser();
        generator = new EcoreMetamodelGenerator(properties);
    }

    /**
     * Starts the Ecore metamodel extraction for a specific project. The project will be parsed and
     * an Ecore metamodel will be build and saved as an Ecore file
     * @param project is the specific project for the extraction.
     */
    public void extractFrom(IProject project) {
        check(project); // check if valid.
        IJavaProject javaProject = JavaCore.create(project); // create java project
        IntermediateModel model = parser.buildIntermediateModel(javaProject);
        generator.generateMetamodelFrom(model);
        generator.saveMetamodel();
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
