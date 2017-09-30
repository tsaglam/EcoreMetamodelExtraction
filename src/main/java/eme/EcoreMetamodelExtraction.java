package eme;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

import eme.extractor.JavaProjectExtractor;
import eme.generator.EcoreMetamodelGenerator;
import eme.generator.GeneratedEcoreMetamodel;
import eme.model.IntermediateModel;
import eme.properties.ExtractionProperties;

/**
 * Main class for Ecore metamodel extraction.
 * @author Timur Saglam
 */
public class EcoreMetamodelExtraction {
    private static final Logger logger = LogManager.getLogger(EcoreMetamodelExtraction.class.getName());
    private final EcoreMetamodelGenerator generator;
    private final JavaProjectExtractor parser;
    private final ExtractionProperties properties;

    /**
     * Basic constructor. Builds {@link JavaProjectExtractor}, {@link EcoreMetamodelGenerator} and
     * {@link GenModelGenerator}.
     */
    public EcoreMetamodelExtraction() {
        logger.info("Started EME...");
        properties = new ExtractionProperties();
        parser = new JavaProjectExtractor();
        generator = new EcoreMetamodelGenerator(properties);
    }

    /**
     * Starts the Ecore metamodel extraction for a specific {@link IProject}. The {@link IProject} will be parsed and an
     * Ecore metamodel will be build.
     * @param project is the specific {@link IProject} for the extraction.
     * @return the Ecore metamodel.
     */
    public GeneratedEcoreMetamodel extract(IProject project) {
        logger.info("Started extraction of project " + project.getName());
        check(project); // check if valid.
        IJavaProject javaProject = JavaCore.create(project); // create java project
        IntermediateModel model = parser.buildIntermediateModel(javaProject);
        GeneratedEcoreMetamodel metamodel = generator.generateMetamodel(model);
        generator.saveMetamodel(); // save metamodel
        return metamodel;
    }

    /**
     * Grants access to the {@link ExtractionProperties}.
     * @return the {@link ExtractionProperties}.
     */
    public ExtractionProperties getProperties() {
        return properties;
    }

    /**
     * Checks whether a specific {@link IProject} is valid (neither null nor nonexistent)
     * @param project is the specific {@link IProject}.
     */
    private void check(IProject project) {
        if (project == null) {
            throw new IllegalArgumentException("Project can't be null!");
        } else if (!project.exists()) {
            throw new IllegalArgumentException("Project " + project.toString() + "does not exist!");
        }
    }
}