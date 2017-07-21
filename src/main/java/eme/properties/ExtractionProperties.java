package eme.properties;

/**
 * This class manages the extraction properties in the user.properties file.
 * @author Timur Saglam
 */
public class ExtractionProperties extends AbstractProperties<TextProperty, BinaryProperty> {
    /**
     * Basic constructor, sets the file name, file description and symbolic bundle name.
     */
    public ExtractionProperties() {
        super("user.properties", "Use this file to configure the Ecore metamodel extraction.", "EcoreMetamodelExtraction");
    }
}
