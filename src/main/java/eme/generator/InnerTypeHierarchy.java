package eme.generator;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;

import eme.properties.ExtractionProperties;
import eme.properties.TextProperty;

/**
 * This class allows to build an inner type package hierarchy from EDataTypes.
 * @author Timur Saglam
 */
public class InnerTypeHierarchy extends EPackageHierarchy {

    /**
     * Basic constructor.
     * @param basePackage is the base {@link EPackage} of hierarchy. This is the package of the (most) outer type.
     * @param properties is the instance of the {@link ExtractionProperties} class.
     */
    public InnerTypeHierarchy(EPackage basePackage, ExtractionProperties properties) {
        super(basePackage, properties);
    }

    /**
     * Adds an {@link EClassifier} to the package hierarchy. Generates the missing packages for the hierarchy.
     * @param classifier is the new {@link EClassifier}.
     * @param relativePath is the relative path of the classifier to the base package. This is used to build the package
     * hierarchy.
     */
    public void add(EClassifier classifier, String relativePath) {
        String suffix = properties.get(TextProperty.NESTED_TYPE_PACKAGE);
        String path = relativePath.replace(".",  suffix + "."); // rename packages to avoid name collisions
        super.add(classifier, packagePath(path)); // add with split up path
    }
}