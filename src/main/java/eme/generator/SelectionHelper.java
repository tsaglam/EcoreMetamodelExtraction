package eme.generator;

import static eme.model.datatypes.AccessLevelModifier.NO_MODIFIER;
import static eme.model.datatypes.AccessLevelModifier.PRIVATE;
import static eme.model.datatypes.AccessLevelModifier.PROTECTED;
import static eme.model.datatypes.AccessLevelModifier.PUBLIC;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import eme.model.ExtractedClass;
import eme.model.ExtractedEnum;
import eme.model.ExtractedInterface;
import eme.model.ExtractedMethod;
import eme.model.ExtractedPackage;
import eme.model.ExtractedType;
import eme.model.MethodType;
import eme.model.datatypes.AccessLevelModifier;
import eme.model.datatypes.ExtractedField;
import eme.properties.BinaryProperty;
import eme.properties.ExtractionProperties;

/**
 * This class helps to decide whether a extracted element may be generated or not. It combines rules from a properties
 * class with the selection states of the model elements themselves.
 * @author Timur Saglam
 */
public class SelectionHelper {
    private static final Logger logger = LogManager.getLogger(SelectionHelper.class.getName());
    private final ExtractionProperties properties;
    private final Map<String, Integer> reportMap;

    /**
     * Simple constructor, sets the {@link ExtractionProperties} object.
     * @param properties are the extraction properties.
     */
    public SelectionHelper(ExtractionProperties properties) {
        this.properties = properties;
        reportMap = new HashMap<String, Integer>();
    }

    /**
     * Checks whether a {@link ExtractedField} may be generated.
     * @param attribute is the extracted attribute.
     * @return true if it may be generated.
     */
    public boolean allowsGenerating(ExtractedField attribute) {
        AccessLevelModifier modifier = attribute.getModifier();
        boolean allowed = !attribute.isStatic() || properties.get(BinaryProperty.STATIC_FIELDS);
        allowed &= modifier != PUBLIC || properties.get(BinaryProperty.PUBLIC_FIELDS);
        allowed &= modifier != NO_MODIFIER || properties.get(BinaryProperty.DEFAULT_FIELDS);
        allowed &= modifier != PROTECTED || properties.get(BinaryProperty.PROTECTED_FIELDS);
        allowed &= modifier != PRIVATE || properties.get(BinaryProperty.PRIVATE_FIELDS);
        return report("attribute", allowed);
    }

    /**
     * Checks whether a {@link ExtractedMethod} may be generated.
     * @param method is the extracted method.
     * @return true if it may be generated.
     */
    public boolean allowsGenerating(ExtractedMethod method) {
        AccessLevelModifier modifier = method.getModifier();
        MethodType type = method.getMethodType();
        boolean allowed = method.isSelected();
        allowed &= type != MethodType.CONSTRUCTOR || properties.get(BinaryProperty.CONSTRUCTORS);
        allowed &= !method.isAbstract() || properties.get(BinaryProperty.ABSTRACT_METHODS);
        allowed &= !method.isStatic() || properties.get(BinaryProperty.STATIC_METHODS);
        allowed &= modifier != NO_MODIFIER || properties.get(BinaryProperty.DEFAULT_METHODS);
        allowed &= modifier != PUBLIC || properties.get(BinaryProperty.PUBLIC_METHODS);
        allowed &= modifier != PROTECTED || properties.get(BinaryProperty.PROTECTED_METHODS);
        allowed &= modifier != PRIVATE || properties.get(BinaryProperty.PRIVATE_METHODS);
        allowed &= type != MethodType.ACCESSOR || properties.get(BinaryProperty.ACCESS_METHODS);
        allowed &= type != MethodType.MUTATOR || properties.get(BinaryProperty.ACCESS_METHODS);
        return report(type.toString(), allowed);
    }

    /**
     * Checks whether a extracted subpackage may be generated.
     * @param subpackage is the subpackage.
     * @return true if it may be generated.
     */
    public boolean allowsGenerating(ExtractedPackage subpackage) {
        boolean allowed = subpackage.isSelected();
        allowed &= !subpackage.isEmpty() || properties.get(BinaryProperty.EMPTY_PACKAGES);
        return report("package", allowed);
    }

    /**
     * Checks whether a {@link ExtractedType} may be generated.
     * @param type is the extracted type.
     * @return true if it may be generated.
     */
    public boolean allowsGenerating(ExtractedType type) {
        boolean allowed = type.isSelected() && (!type.isInnerType() || properties.get(BinaryProperty.NESTED_TYPES));
        if (type instanceof ExtractedClass) {
            allowed &= properties.get(BinaryProperty.CLASSES);
            allowed &= !((ExtractedClass) type).isThrowable() || properties.get(BinaryProperty.THROWABLES);
        } else if (type instanceof ExtractedInterface) {
            allowed &= properties.get(BinaryProperty.INTERFACES);
        } else if (type instanceof ExtractedEnum) {
            allowed &= properties.get(BinaryProperty.ENUMS);
        }
        return report(type.getClass().getSimpleName().substring(9).toLowerCase(), allowed); // class, interface, enum
    }

    /**
     * Checks whether final fields are represented through unchangeable EStructuralFeatures.
     * @return true if they are.
     */
    public boolean allowUnchangeable() {
        return properties.get(BinaryProperty.FINAL_AS_UNCHANGEABLE);
    }

    /**
     * Generates a report about the ungenerated elements. The report counts for the different elements of the
     * intermediate models how many of them were not generated due to selection or properties.
     */
    public void generateReport() {
        if (reportMap.isEmpty()) {
            logger.info("There were no ungenerated elements.");
        } else {
            logger.info("There were ungenerated elements because of selection and/or properties:");
            LinkedList<String> list = new LinkedList<String>(reportMap.keySet());
            Collections.sort(list); // sort keys
            String pluralSuffix;
            for (String element : list) { // for every reported element
                pluralSuffix = element.endsWith("s") ? "es" : "s"; // add plural suffix
                logger.info("   " + element + pluralSuffix + ": " + reportMap.get(element)); // print
            }
        }
    }

    /**
     * Accessor method for the {@link ExtractionProperties} of the selection helper.
     * @return the properties.
     */
    public ExtractionProperties getProperties() {
        return properties;
    }

    /**
     * Increases the number of ungenerated features for a specific type of features.
     * @param allowed specifies whether the generation should be allowed or not.
     * @param feature is the specific type of features.
     * @return the value of the parameter allowed
     */
    private boolean report(String feature, boolean allowed) {
        if (!allowed) { // if generating was not allowed:
            if (reportMap.containsKey(feature)) { // if has already reported on feature
                reportMap.put(feature, reportMap.get(feature) + 1); // increase
            } else { // if never reported on feature
                reportMap.put(feature, 1); // create new entry for feature
            }
        }
        return allowed; // return whether it was allowed or not.
    }
}