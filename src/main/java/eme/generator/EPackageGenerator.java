package eme.generator;

import static eme.properties.BinaryProperty.DUMMY_CLASS;
import static eme.properties.BinaryProperty.ROOT_CONTAINER;
import static eme.properties.TextProperty.DUMMY_NAME;
import static eme.properties.TextProperty.ROOT_NAME;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;

import eme.generator.hierarchies.InnerTypeHierarchy;
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
    private void addTypes(EPackage ePackage, ExtractedPackage extractedPackage) {
        for (ExtractedType type : extractedPackage.getTypes()) { // for all types
            if (selector.allowsGenerating(type)) { // if is allowed to
                EClassifier eClassifier = classGenerator.generateEClassifier(type);
                if (type.isInnerType()) { // get relative path of inner type to current package:
                    String relativePath = type.getFullName().replace(extractedPackage.getFullName() + '.', "");
                    new InnerTypeHierarchy(ePackage, properties).add(eClassifier, relativePath); // add inner type
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
            ePackage.setNsURI(getURI(extractedPackage));
        }
        addSubpackages(ePackage, extractedPackage);
        addTypes(ePackage, extractedPackage);
        return ePackage;
    }

    /**
     * Generates empty root package from the {@link IntermediateModel}.
     */
    private EPackage generateRoot() {
        EPackage root = ecoreFactory.createEPackage(); // create object
        String name = properties.get(TextProperty.DEFAULT_PACKAGE);
        root.setName(name); // set default name
        root.setNsPrefix(name); // set default prefix
        root.setNsURI(getRootURI());
        classGenerator = new EClassifierGenerator(model, root, selector);
        generateRootElement(root);
        return root;
    }

    /**
     * Generates the root element which is either a root container or a dummy class.
     */
    private void generateRootElement(EPackage root) {
        EClass rootElement = null;
        if (properties.get(ROOT_CONTAINER)) { // chose root container if both are enabled.
            rootElement = classGenerator.generateRootContainer(properties.get(ROOT_NAME));
        } else if (properties.get(DUMMY_CLASS)) {
            rootElement = classGenerator.generateDummy(properties.get(DUMMY_NAME));
        }
        root.getEClassifiers().add(rootElement);
    }

    /**
     * Builds the URI of an root {@link EPackage} from an {@link ExtractedPackage}. The URI contains the project name
     * and the default package name (optionally).
     */
    private String getRootURI() {
        System.err.println("DEF PKG: " + TextProperty.DEFAULT_PACKAGE);
        return model.getProjectName() + "/" + properties.get(TextProperty.DEFAULT_PACKAGE);
    }

    /**
     * Builds the URI of an {@link EPackage} from an {@link ExtractedPackage}. The URI contains the project name, the
     * default package name (optionally), and the full package name.
     */
    private String getURI(ExtractedPackage extractedPackage) {
        return getRootURI() + "." + extractedPackage.getFullName();
    }
}