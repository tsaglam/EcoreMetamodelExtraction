package eme.generator;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import eme.generator.saving.AbstractSavingStrategy;
import eme.generator.saving.NewProjectSavingStrategy;
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
        savingStrategy = new NewProjectSavingStrategy();
    }

    /**
     * Method starts the Ecore metamodel generation.
     * @param model is the intermediate model that is the source for the generator.
     */
    public void generateMetamodelFrom(IntermediateModel model) {
        ExtractedPackage root = model.getRoot(); // get root package.
        if (root == null) { // check if valid.
            throw new IllegalArgumentException("The root of an model can't be null: " + model.toString());
        }
        projectName = model.getProjectName(); // get project name.
        ecoreMetamodel = generateEPackage(root); // generate ecore class structure.
        savingAlgorithmPrototype(ecoreMetamodel); // TODO (HIGH) remove this later and use save()
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
            ePackage.setName("DEFAULT");
            ePackage.setNsPrefix("DEFAULT"); // TODO (MEDIUM) make those settable.
            ePackage.setNsURI("http://www.eme.org/" + projectName);
        } else {
            ePackage.setName(extractedPackage.getName());
        }
        for (ExtractedPackage subpackage : extractedPackage.getSubpackages()) {
            ePackage.getESubpackages().add(generateEPackage(subpackage));
        } // TODO (MEDIUM) Remove duplicate code.
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

    /**
     * IMPORTANT: Prototypical method for saving an EPackage as ecore file. The method currently
     * uses an existing project and a fixed path. To work it requires to have an EMF Project called
     * "EME-Generator-Output" in the workspace. TODO (HIGH) remove this when custom saving works.
     */
    private void savingAlgorithmPrototype(EPackage ePackage) {
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        String ecoreFilePath = workspace.getRoot().getLocation().toFile().getPath() + "/EME-Generator-Output/model/";
        String ecoreFileName = projectName + "-" + LocalDate.now() + "-" + LocalTime.now();
        ePackage.eClass(); // Initialize the EPackage:
        ResourceSet resourceSet = new ResourceSetImpl(); // get new resource set
        Resource resource = null; // create a resource:
        try {
            resource = resourceSet.createResource(URI.createFileURI(ecoreFilePath + ecoreFileName + ".ecore"));
        } catch (IllegalArgumentException exception) {
            exception.printStackTrace();
        }
        resource.getContents().add(ePackage); // add the EPackage as root.
        try { // save the content:
            resource.save(Collections.EMPTY_MAP);
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        IWorkspaceRoot root = workspace.getRoot();
        IContainer folder = root.getContainerForLocation(new Path(ecoreFilePath));
        try {
            folder.refreshLocal(IResource.DEPTH_INFINITE, null);
        } catch (CoreException exception) {
            exception.printStackTrace();
        }
    }

}
