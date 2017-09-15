package eme.generator;

import org.eclipse.emf.ecore.EPackage;

import eme.generator.saving.SavingInformation;
import eme.model.IntermediateModel;

/**
 * This is a container class for generated Ecore metamodels. It contains a reference to the metamodels root element and
 * the metamodels intermediate model. Additionally, it can contain saving information.
 * @author Timur Saglam
 */
public class GeneratedEcoreMetamodel {
    private final IntermediateModel intermediateModel;
    private final EPackage root;
    private SavingInformation savingInformation;

    /**
     * Basic constructor.
     * @param root is the root {@link EPackage} of the generated Ecore metamodel.
     * @param intermediateModel is the {@link IntermediateModel} which was used to generate the Ecore metamodel.
     */
    public GeneratedEcoreMetamodel(EPackage root, IntermediateModel intermediateModel) {
        this.root = root;
        this.intermediateModel = intermediateModel;
    }

    /**
     * Returns the the {@link IntermediateModel} which was used to generate the Ecore metamodel.
     * @return the {@link IntermediateModel} instance.
     */
    public IntermediateModel getIntermediateModel() {
        return intermediateModel;
    }

    /**
     * Accessor for the root {@link EPackage}.
     * @return the root.
     */
    public EPackage getRoot() {
        return root;
    }

    /**
     * Accessor for the {@link SavingInformation} object.
     * @return the savingInformation
     */
    public SavingInformation getSavingInformation() {
        if (savingInformation == null) {
            throw new IllegalStateException("Generated Ecore Metamodel was not saved. There is no saving information available.");
        }
        return savingInformation;
    }

    /**
     * Checks whether this metamodel was already saved. This means is contains saving informations (a
     * {@link SavingInformation} object).
     * @return true if it was.
     */
    public boolean isSaved() {
        return savingInformation != null;
    }

    /**
     * Mutator for the {@link SavingInformation} object.
     * @param savingInformation the savingInformation to set
     */
    public void setSavingInformation(SavingInformation savingInformation) {
        this.savingInformation = savingInformation;
    }
}