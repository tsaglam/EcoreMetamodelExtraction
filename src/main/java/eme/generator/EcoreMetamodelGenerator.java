package eme.generator;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EPackage;

import eme.generator.saving.AbstractSavingStrategy;
import eme.generator.saving.CustomPathSavingStrategy;
import eme.generator.saving.NewProjectSavingStrategy;
import eme.generator.saving.OutputProjectSavingStrategy;
import eme.generator.saving.SameProjectSavingStrategy;
import eme.generator.saving.SavingInformation;
import eme.model.ExtractedPackage;
import eme.model.IntermediateModel;
import eme.properties.ExtractionProperties;

/**
 * This class generates an Ecore Metamodel from an Intermediate Model. It also allows to save a generated metamodel as
 * an Ecore file using a specific saving strategy.
 * @author Timur Saglam
 */
public class EcoreMetamodelGenerator {
    private static final Logger logger = LogManager.getLogger(EcoreMetamodelGenerator.class.getName());
    private static final String OUTPUT_PROJECT = "EME-Generator-Output";
    private final EPackageGenerator ePackageGenerator;
    private EPackage metamodel;
    private String projectName;
    private AbstractSavingStrategy savingStrategy;

    /**
     * Basic constructor.
     * @param properties is the ExtractionProperties class for the exraction.
     */
    public EcoreMetamodelGenerator(ExtractionProperties properties) {
        ePackageGenerator = new EPackageGenerator(properties); // build generators
        changeSavingStrategy(properties.getSavingStrategy()); // set saving strategy
    }

    /**
     * Changes the {@link AbstractSavingStrategy} to a new one.
     * @param strategyName is the name of the new saving strategy.
     */
    public void changeSavingStrategy(String strategyName) { // Add custom strategies here
        if ("OutputProject".equals(strategyName)) {
            savingStrategy = new OutputProjectSavingStrategy(OUTPUT_PROJECT);
        } else if ("SameProject".equals(strategyName)) {
            savingStrategy = new SameProjectSavingStrategy();
        } else if ("CustomPath".equals(strategyName)) {
            savingStrategy = new CustomPathSavingStrategy();
        } else {
            savingStrategy = new NewProjectSavingStrategy();
        }
    }

    /**
     * Method starts the Ecore metamodel generation.
     * @param model is the intermediate model that is the source for the generator.
     * @return the root element of the metamodel, an EPackage.
     */
    public EPackage generateMetamodelFrom(IntermediateModel model) {
        logger.info("Started generating the metamodel...");
        ExtractedPackage root = model.getRoot(); // get root package.
        if (root == null || !root.isSelected()) { // check if valid.
            throw new IllegalArgumentException("The root of an model can't be null or deselected: " + model.toString());
        }
        projectName = model.getProjectName(); // store project name.
        metamodel = ePackageGenerator.generate(model); // generate model model.
        return metamodel;
    }

    /**
     * Saves the metamodel as an Ecore file.
     * @return the saving information.
     */
    public SavingInformation saveMetamodel() {
        logger.info("Started saving the metamodel");
        if (metamodel == null) {
            throw new IllegalStateException("Cannot save Ecore metamodel before extracting one.");
        }
        return savingStrategy.save(metamodel, projectName);
    }
}