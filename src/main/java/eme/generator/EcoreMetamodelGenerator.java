package eme.generator;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;

import eme.generator.saving.AbstractSavingStrategy;
import eme.generator.saving.OutputProjectSavingStrategy;
import eme.model.ExtractedClass;
import eme.model.ExtractedEnumeration;
import eme.model.ExtractedInterface;
import eme.model.ExtractedPackage;
import eme.model.IntermediateModel;

/**
 * This class generates an Ecore Metamodel from an Intermediate Model.
 * @author Timur Saglam
 */
public class EcoreMetamodelGenerator {

    private static String TOP_LEVEL_PACKAGE_NAME = "DEFAULT"; // TODO (MEDIUM) use property
    private static String TOP_LEVEL_PACKAGE_PREFIX = "DEFAULT";
    private EcoreFactory ecoreFactory;
    private EPackage ecoreMetamodel;
    private String projectName;
    private AbstractSavingStrategy savingStrategy;

    /**
     * Basic constructor.
     */
    public EcoreMetamodelGenerator() {
        ecoreFactory = EcoreFactory.eINSTANCE;
        projectName = "unknown-project";
        savingStrategy = new OutputProjectSavingStrategy("EME-Generator-Output");
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
        ecoreMetamodel = generateEPackage(root); // generate ecore class structure.
        return ecoreMetamodel;
    }

    /**
     * Saves the metamodel as an Ecore file.
     */
    public void saveMetamodel() {
        savingStrategy.save(ecoreMetamodel, projectName);
    }

    /**
     * Generates an EClass from an ExtractedClass.
     * @param extractedClass is the ExtractedClass.
     * @return the EClass.
     */
    private EClass generateEClass(ExtractedClass extractedClass) {
        EClass eClass = ecoreFactory.createEClass();
        eClass.setName(extractedClass.getName());
        eClass.setAbstract(extractedClass.isAbstract());
        return eClass;
    }

    /**
     * Generates an EClass from an ExtractedInterface.
     * @param extractedInterface is the ExtractedInterface.
     * @return the EClass.
     */
    private EClass generateEClass(ExtractedInterface extractedInterface) {
        EClass eClass = ecoreFactory.createEClass();
        eClass.setName(extractedInterface.getName());
        eClass.setAbstract(true);
        eClass.setInterface(true);
        return eClass;
    }

    /**
     * Generates an EEnum from an ExtractedEnumeration.
     * @param extractedEnum is the ExtractedEnumeration.
     * @return the EEnum.
     */
    private EEnum generateEEnum(ExtractedEnumeration extractedEnum) {
        EEnum eEnum = ecoreFactory.createEEnum(); // create EEnum
        eEnum.setName(extractedEnum.getName()); // set name
        for (String enumeral : extractedEnum.getEnumerals()) { // for very Enumeral
            EEnumLiteral literal = ecoreFactory.createEEnumLiteral(); // create literal
            literal.setName(enumeral); // set name.
            literal.setValue(eEnum.getELiterals().size()); // set ordinal.
            eEnum.getELiterals().add(literal); // add literal to enumm.
        }
        return eEnum;
    }

    /**
     * Generates an EPackage from an extractedPackage. Recursively calls this method to all
     * contained elements.
     * @param extractedPackage is the package the EPackage gets generated from.
     * @return the generated EPackage.
     */
    private EPackage generateEPackage(ExtractedPackage extractedPackage) {
        EPackage ePackage = ecoreFactory.createEPackage();
        if (extractedPackage.isRoot()) {
            ePackage.setName(TOP_LEVEL_PACKAGE_NAME);
            ePackage.setNsPrefix(TOP_LEVEL_PACKAGE_PREFIX);
        } else {
            ePackage.setName(extractedPackage.getName());
            ePackage.setNsPrefix(extractedPackage.getName());
        }
        ePackage.setNsURI(projectName+"/"+extractedPackage.getFullName());
        for (ExtractedPackage subpackage : extractedPackage.getSubpackages()) {
            ePackage.getESubpackages().add(generateEPackage(subpackage));
        } // TODO (MEDIUM) Improve code style.
        for (ExtractedClass extractedClass : extractedPackage.getClasses()) {
            ePackage.getEClassifiers().add(generateEClass(extractedClass));
        }
        for (ExtractedInterface extractedInterface : extractedPackage.getInterfaces()) {
            ePackage.getEClassifiers().add(generateEClass(extractedInterface));
        }
        for (ExtractedEnumeration extractedEnum : extractedPackage.getEnumerations()) {
            ePackage.getEClassifiers().add(generateEEnum(extractedEnum));
        }
        return ePackage;
    }
}
