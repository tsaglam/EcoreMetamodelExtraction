package eme;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

import eme.generator.EcoreMetamodelGenerator;
import eme.model.IntermediateModel;
import eme.parser.JavaProjectParser;
import eme.properties.ExtractionProperties;

/**
 * Base class of the prototype for Ecore metamodel extraction.
 * @author Timur Saglam
 */
public class EcoreMetamodelExtraction {
    private static final Logger logger = LogManager.getLogger(JavaProjectParser.class.getName());
    private final ExtractionProperties properties;
    private final JavaProjectParser parser;
    private final EcoreMetamodelGenerator generator;

    /**
     * Basic constructor. Builds parser and generator.
     */
    public EcoreMetamodelExtraction() {
        BasicConfigurator.configure();
        logger.info("Started EME");
        properties = new ExtractionProperties();
        parser = new JavaProjectParser();
        generator = new EcoreMetamodelGenerator(properties);
    }

    /**
     * Starts the Ecore metamodel extraction for a specific project. The project will be parsed and
     * an Ecore metamodel will be build.
     * @param project is the specific project for the extraction.
     * @return the Ecore metamodel with the default package as root.
     */
    public EPackage extractFrom(IProject project) {
        check(project); // check if valid.
        IJavaProject javaProject = JavaCore.create(project); // create java project
        IntermediateModel model = parser.buildIntermediateModel(javaProject);
        return generator.generateMetamodelFrom(model);
    }

    /**
     * Starts the Ecore metamodel extraction for a specific project. The project will be parsed and
     * an Ecore metamodel will be build and saved as an Ecore file
     * @param project is the specific project for the extraction.
     */
    public void extractAndSaveFrom(IProject project) {
        extractFrom(project);
        generator.saveMetamodel();
    }

    /**
     * Grants access to the properties.
     * @return the ExtractionProperties.
     */
    public ExtractionProperties getProperties() {
        return properties;
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
