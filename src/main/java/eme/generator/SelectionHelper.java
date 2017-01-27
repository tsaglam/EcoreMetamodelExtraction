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
import eme.model.ExtractedMethod;
import eme.model.ExtractedPackage;
import eme.model.ExtractedType;
import eme.model.MethodType;
import eme.model.datatypes.AccessLevelModifier;
import eme.model.datatypes.ExtractedAttribute;
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
     * Checks whether a {@link ExtractedAttribute} may be generated.
     * @param attribute is the extracted attribute.
     * @return true if it may be generated.
     */
    public boolean allowsGenerating(ExtractedAttribute attribute) {
        AccessLevelModifier modifier = attribute.getModifier();
        if ((!attribute.isStatic() || properties.getExtractStaticMethods()) && (modifier != PUBLIC || properties.getExtractPublicAttributes())
                && (modifier != NO_MODIFIER || properties.getExtractDefaultAttributes())  // extract default attributes
                && (modifier != PROTECTED || properties.getExtractProtectedAttributes()) // extract protected attributes
                && (modifier != PRIVATE || properties.getExtractPrivateAttributes())) { // extract private attributes
            return true;
        }
        report("attribute");
        return false;
    }

    /**
     * Checks whether a {@link ExtractedMethod} may be generated.
     * @param method is the extracted method.
     * @return true if it may be generated.
     */
    public boolean allowsGenerating(ExtractedMethod method) {
        AccessLevelModifier modifier = method.getModifier();
        MethodType type = method.getMethodType();
        if (method.isSelected() && (type != MethodType.CONSTRUCTOR || properties.getExtractConstructors())
                && (!method.isAbstract() || properties.getExtractAbstractMethods()) // extract abstract methods
                && (!method.isStatic() || properties.getExtractStaticMethods()) // extract static methods
                && (modifier != NO_MODIFIER || properties.getExtractDefaultMethods()) // extract default methods
                && (modifier != PROTECTED || properties.getExtractProtectedMethods()) // extract protected methods
                && (modifier != PRIVATE || properties.getExtractPrivateMethods()) // extract private methods
                && (type != MethodType.ACCESSOR || properties.getExtractAccessMethods())  // extract accessors
                && (type != MethodType.MUTATOR || properties.getExtractAccessMethods())) { // extract mutators
            return true;
        } else {
            report(type.toString());
        }
        return false;
    }

    /**
     * Checks whether a extracted subpackage may be generated.
     * @param subpackage is the subpackage.
     * @return true if it may be generated.
     */
    public boolean allowsGenerating(ExtractedPackage subpackage) {
        if (subpackage.isSelected() && (!subpackage.isEmpty() || properties.getExtractEmptyPackages())) {
            return true;
        }
        report("package");
        return false;
    }

    /**
     * Checks whether a {@link ExtractedType} may be generated.
     * @param type is the extracted type.
     * @return true if it may be generated.
     */
    public boolean allowsGenerating(ExtractedType type) {
        if (type.isSelected() && (!type.isInnerType() || properties.getExtractNestedTypes())) {
            if (type instanceof ExtractedClass) {
                return !((ExtractedClass) type).isThrowable() || properties.getExtractThrowables();
            } else {
                return true;
            }
        }
        report(type.getClass().getSimpleName().substring(9).toLowerCase()); // Class, Interface, Enumeration
        return false;
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
     * @param feature is the specific type of features.
     */
    private void report(String feature) {
        if (reportMap.containsKey(feature)) {
            reportMap.put(feature, reportMap.get(feature) + 1);
        } else {
            reportMap.put(feature, 1);
        }
    }
}