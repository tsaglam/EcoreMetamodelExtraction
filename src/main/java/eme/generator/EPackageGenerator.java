package eme.generator;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;

import eme.model.ExtractedPackage;
import eme.model.ExtractedType;
import eme.model.IntermediateModel;
import eme.properties.ExtractionProperties;

/**
 * Generator class for Ecore packages.
 * @author Timur Saglam
 */
public class EPackageGenerator {
    private EClassifierGenerator classifierGenerator;
    private final EcoreFactory ecoreFactory;
    private IntermediateModel model;
    private final ExtractionProperties properties;
    private final SelectionHelper selector;

    /**
     * Basic constructor.
     * @param properties is the {@link ExtractionProperties} class for the extraction.
     * @param model is the {@link IntermediateModel} instance which is used to extract a metamodel.
     */
    public EPackageGenerator(ExtractionProperties properties, IntermediateModel model) {
        this.properties = properties;
        this.model = model; // set model
        ecoreFactory = EcoreFactory.eINSTANCE;
        selector = new SelectionHelper(properties); // build selection helper
    }

    /**
     * Generates an Ecore metamodel from the {@link IntermediateModel} of the {@link EPackageGenerator}.
     * @return the root {@link EPackage} of the Ecore metamodel.
     */
    public EPackage generate() {
        EPackage eRoot = generateEPackage(model.getRoot()); // generate base model:
        classifierGenerator.completeEClassifiers(); // complete EClasses

        selector.generateReport(); // print reports
        return eRoot; // return Ecore metamodel root package
    }

    /**
     * Generates an EPackage from an extractedPackage. Recursively calls this method to all contained elements.
     */
    private EPackage generateEPackage(ExtractedPackage extractedPackage) {
        EPackage ePackage;
        if (extractedPackage.isRoot()) { // set root name & prefix:
            ePackage = generateRoot();
        } else { // set name & prefix for non root packages:
            ePackage = ecoreFactory.createEPackage();
            ePackage.setName(extractedPackage.getName());
            ePackage.setNsPrefix(extractedPackage.getName());
        }
        ePackage.setNsURI(model.getProjectName() + "/" + extractedPackage.getFullName()); // Set URI
        for (ExtractedPackage subpackage : extractedPackage.getSubpackages()) { // for all packages
            if (selector.allowsGenerating(subpackage)) { // if is allowed to
                ePackage.getESubpackages().add(generateEPackage(subpackage)); // extract
            }
        }
        for (ExtractedType type : extractedPackage.getTypes()) { // for all types
            if (selector.allowsGenerating(type)) { // if is allowed to
                ePackage.getEClassifiers().add(classifierGenerator.generateEClassifier(type)); // extract
            }
        }
        return ePackage;
    }

    /**
     * Generates empty root package from the intermediate model. URI is not set.
     */
    private EPackage generateRoot() {
        EPackage root = ecoreFactory.createEPackage(); // create object
        root.setName(properties.getDefaultPackageName()); // set default name
        root.setNsPrefix(properties.getDefaultPackageName()); // set default prefix
        classifierGenerator = new EClassifierGenerator(model, root, selector);
        return root;
    }
}