package eme.generator;

import org.eclipse.emf.ecore.EPackage;

import eme.generator.saving.SavingInformation;

/**
 * This is a container class for generated Ecore metamodels.
 * @author Timur Saglam
 */
public class GeneratedEcoreMetamodel {
    private final EPackage root;
    private SavingInformation savingInformation;

    /**
     * Basic constructor.
     * @param root is the root {@link EPackage} of the generated Ecore metamodel.
     */
    public GeneratedEcoreMetamodel(EPackage root) {
        this.root = root;
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
            throw new IllegalStateException("Generated Ecore Metamodel is not saved. There is no saving information available.");
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