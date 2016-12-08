package eme.generator;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import eme.model.ExtractedPackage;
import eme.model.IntermediateModel;

/**
 * This class generates an Ecore Metamodel from an Intermediate Model.
 * @author Timur Saglam
 */
public class EcoreMetamodelGenerator {

    private EcoreFactory ecoreFactory;
    private String projectName;

    /**
     * Basic constructor.
     */
    public EcoreMetamodelGenerator() {
        ecoreFactory = EcoreFactory.eINSTANCE;
        projectName = "unknown-project";
    }

    /**
     * Method starts the Ecore metamodel generation.
     * @param model is the intermediate model that is the source for the generator.
     */
    public void generateFrom(IntermediateModel model) {
        ExtractedPackage root = model.getRoot(); // get root package.
        if (root == null) { // check if valid.
            throw new IllegalArgumentException("The root of an model can't be null: " + model.toString());
        }
        projectName = model.getProjectName(); // get project name.
        EPackage eRoot = generateEPackage(root); // generate ecore class structure.
        savingAlgorithmPrototype(eRoot); // TODO (HIGH) create real saving method
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
        }
        return ePackage;
    }

    /**
     * Prototypical method for saving an EPackage as ecore file. It currently uses an existing
     * project and a fixed path. IMPORTANT: This method currently requires to have an EMF Project
     * called "GeneratorOutput" in the workspace. The EMF project should contain a folder model. The
     * generated Ecore files can be seen in this folder after refreshing the folder.
     */
    private void savingAlgorithmPrototype(EPackage ePackage) {
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        String ecoreFilePath = workspace.getRoot().getLocation().toFile().getPath() + "/GeneratorOutput/model/";
        String ecoreFileName = projectName + "-" + ePackage.hashCode();
        ePackage.eClass(); // Initialize the EPackage:
        Resource.Factory.Registry registry = Resource.Factory.Registry.INSTANCE;
        Map<String, Object> map = registry.getExtensionToFactoryMap();
        map.put(EcorePackage.eNAME, new XMIResourceFactoryImpl());  // add default extension
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
    }

}
