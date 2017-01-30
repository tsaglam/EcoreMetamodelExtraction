package eme.generator;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;

import eme.model.ExtractedPackage;
import eme.model.ExtractedType;
import eme.model.IntermediateModel;
import eme.properties.ExtractionProperties;
import eme.properties.TextProperty;

/**
 * Generator class for Ecore packages ({@link EPackage}s).
 * @author Timur Saglam
 */
public class EPackageGenerator {
    private EClassifierGenerator classGenerator;
    private final EcoreFactory ecoreFactory;
    private IntermediateModel model;
    private final ExtractionProperties properties;
    private final SelectionHelper selector;

    /**
     * Basic constructor, sets the properties.
     * @param properties is the {@link ExtractionProperties} class for the extraction.
     */
    public EPackageGenerator(ExtractionProperties properties) {
        this.properties = properties;
        ecoreFactory = EcoreFactory.eINSTANCE;
        selector = new SelectionHelper(properties); // build selection helper
    }

    /**
     * Generates an Ecore metamodel from an {@link IntermediateModel}.
     * @param model is the {@link IntermediateModel}.
     * @return the root {@link EPackage} of the Ecore metamodel.
     */
    public EPackage generate(IntermediateModel model) {
        this.model = model; // set model
        EPackage eRoot = generateEPackage(model.getRoot()); // generate base model:
        classGenerator.completeEClassifiers(); // complete EClasses
        selector.generateReport(); // print reports
        return eRoot; // return Ecore metamodel root package
    }

    /**
     * Adds subpackages to the {@link EPackage}.
     */
    private void addSubpackages(EPackage ePackage, ExtractedPackage extractedPackage) {
        for (ExtractedPackage subpackage : extractedPackage.getSubpackages()) { // for all packages
            if (selector.allowsGenerating(subpackage)) { // if is allowed to
                ePackage.getESubpackages().add(generateEPackage(subpackage)); // extract
            }
        }
    }

    /**
     * Adds types to the package with the help of the {@link EClassifierGenerator}.
     */
    private void addTypes(EPackage ePackage, ExtractedPackage extractedPackage) { // TODO (HIGH) in which class?
        for (ExtractedType type : extractedPackage.getTypes()) { // for all types
            if (selector.allowsGenerating(type)) { // if is allowed to
                EClassifier eClassifier = classGenerator.generateEClassifier(type);
                if (type.isInnerType()) {
                    String path = type.getFullName().replace(extractedPackage.getFullName() + '.', ""); // relative path
                    path = path.replace(".", properties.get(TextProperty.NESTED_TYPE_PACKAGE) + "."); // custom packages
                    new EPackageHierarchy(ePackage).add(eClassifier, path); // build inner type package structure
                } else { // add normal type directly
                    ePackage.getEClassifiers().add(eClassifier); // extract
                }
            }
        }
    }

    /**
     * Generates an {@link EPackage} from an {@link ExtractedPackage}. Recursively calls this method to all contained
     * elements.
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
        addSubpackages(ePackage, extractedPackage);
        addTypes(ePackage, extractedPackage);
        return ePackage;
    }

    /**
     * Generates empty root package from the {@link IntermediateModel}. URI is not set.
     */
    private EPackage generateRoot() {
        EPackage root = ecoreFactory.createEPackage(); // create object
        root.setName(properties.get(TextProperty.DEFAULT_PACKAGE)); // set default name
        root.setNsPrefix(properties.get(TextProperty.DEFAULT_PACKAGE)); // set default prefix
        classGenerator = new EClassifierGenerator(model, root, selector);
        return root;
    }
}