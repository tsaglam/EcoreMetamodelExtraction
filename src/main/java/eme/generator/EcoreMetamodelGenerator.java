package eme.generator;

import org.eclipse.emf.ecore.EPackage;

import eme.generator.saving.AbstractSavingStrategy;
import eme.generator.saving.CustomPathSavingStrategy;
import eme.generator.saving.NewProjectSavingStrategy;
import eme.generator.saving.OutputProjectSavingStrategy;
import eme.generator.saving.SameProjectSavingStrategy;
import eme.model.ExtractedPackage;
import eme.model.IntermediateModel;
import eme.properties.ExtractionProperties;

/**
 * This class generates an Ecore Metamodel from an Intermediate Model. It also allows to save a
 * generated metamodel as an Ecore file using a specific saving strategy.
 * @author Timur Saglam
 */
public class EcoreMetamodelGenerator {

    private EClassGenerator eClassGenerator;
    private EPackage ecoreMetamodel;
    private String projectName;
    private AbstractSavingStrategy savingStrategy;

    /**
     * Basic constructor.
     */
    public EcoreMetamodelGenerator(ExtractionProperties properties) {
        eClassGenerator = new EClassGenerator(properties);
        String strategy = properties.getSavingStrategy();
        if (strategy.equals("OutputProject")) {
            savingStrategy = new OutputProjectSavingStrategy("EME-Generator-Output");
        } else if (strategy.equals("SameProject")) {
            savingStrategy = new SameProjectSavingStrategy();
        } else if (strategy.equals("CustomPath")) {
            savingStrategy = new CustomPathSavingStrategy();
        } else {
            savingStrategy = new NewProjectSavingStrategy();
        }
    }

    /**
     * Changes the saving strategy to a new one.
     * @param newStrategy is the new saving strategy.
     */
    public void changeSavingStrategy(AbstractSavingStrategy newStrategy) {
        if (newStrategy == null) {
            throw new IllegalArgumentException("The new strategy cannot be null!");
        }
        savingStrategy = newStrategy;
    }

    /**
     * Method starts the Ecore metamodel generation.
     * @param model is the intermediate model that is the source for the generator.
     * @return the root element of the metamodel, an EPackage.
     */
    public EPackage generateMetamodelFrom(IntermediateModel model) {
        ExtractedPackage root = model.getRoot(); // get root package.
        if (root == null) { // check if valid.
            throw new IllegalArgumentException("The root of an model can't be null: " + model.toString());
        }
        projectName = model.getProjectName(); // get project name.
        ecoreMetamodel = eClassGenerator.generateEPackage(root, projectName); // generate model.
        return ecoreMetamodel;
    }

    /**
     * Saves the metamodel as an Ecore file.
     */
    public void saveMetamodel() {
        if (ecoreMetamodel == null) {
            throw new IllegalStateException("Cannot save Ecore metamodel before extracting one.");
        }
        savingStrategy.save(ecoreMetamodel, projectName);
    }
}