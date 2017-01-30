package eme.generator;

import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EPackage;

import eme.properties.ExtractionProperties;
import eme.properties.TextProperty;

/**
 * This class allows to build an external type package hierarchy from EDataTypes.
 * @author Timur Saglam
 */
public class ExternalTypeHierarchy extends EPackageHierarchy {

    /**
     * Simple constructor, builds the base for the hierarchy.
     * @param root is the root {@link EPackage} of the metamodel.
     * @param properties is the instance of the {@link ExtractionProperties} class.
     */
    public ExternalTypeHierarchy(EPackage root, ExtractionProperties properties) {
        super(generatePackage(properties.get(TextProperty.DATATYPE_PACKAGE), root), properties);
    }

    /**
     * Adds an {@link EDataType} to the package hierarchy. Generates the missing packages for the hierarchy.
     * @param dataType is the new {@link EDataType}.
     */
    public void add(EDataType dataType) {
        String[] path = packagePath(dataType.getInstanceTypeName()); // get packages from name
        add(dataType, path); // call super class
    }
}