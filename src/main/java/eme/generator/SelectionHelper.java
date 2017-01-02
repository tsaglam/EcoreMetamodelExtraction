package eme.generator;

import static eme.model.datatypes.AccessLevelModifier.NO_MODIFIER;
import static eme.model.datatypes.AccessLevelModifier.PRIVATE;
import static eme.model.datatypes.AccessLevelModifier.PROTECTED;
import static eme.model.datatypes.AccessLevelModifier.PUBLIC;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import eme.model.ExtractedMethod;
import eme.model.ExtractedPackage;
import eme.model.ExtractedType;
import eme.model.datatypes.AccessLevelModifier;
import eme.model.datatypes.ExtractedAttribute;
import eme.parser.JavaProjectParser;
import eme.properties.ExtractionProperties;

/**
 * This class helps to decide whether a extracted element may be generated or not. It combines rules from a properties
 * class with the selection states of the model elements themselves.
 * @author Timur Saglam
 */
public class SelectionHelper {
    private static final Logger logger = LogManager.getLogger(JavaProjectParser.class.getName());
    private final ExtractionProperties properties;
    private final Map<String, Integer> reportMap;

    /**
     * Simple constructor, sets the properties object.
     * @param properties are the extraction properties.
     */
    public SelectionHelper(ExtractionProperties properties) {
        this.properties = properties;
        reportMap = new HashMap<String, Integer>();
    }

    /**
     * Checks whether a extracted attribute may be generated.
     * @param attribute is the extracted attribute.
     * @return true if it may be generated.
     */
    public boolean allowsGenerating(ExtractedAttribute attribute) {
        AccessLevelModifier modifier = attribute.getModifier();
        if ((!attribute.isStatic() || properties.getExtractStaticMethods()) && (modifier != PUBLIC || properties.getExtractProtectedAttributes())
                && (modifier != NO_MODIFIER || properties.getExtractDefaultAttributes())  // extract default attributes
                && (modifier != PROTECTED || properties.getExtractProtectedAttributes()) // extract protected attributes
                && (modifier != PRIVATE || properties.getExtractPrivateAttributes())) { // extract private attributes
            return true;
        }
        report("Attribute");
        return false;
    }

    /**
     * Checks whether a extracted method may be generated.
     * @param method is the extracted method.
     * @return true if it may be generated.
     */
    public boolean allowsGenerating(ExtractedMethod method) {
        AccessLevelModifier modifier = method.getModifier();
        if (method.isSelected() && (!method.isConstructor() || properties.getExtractConstructors())
                && (!method.isAbstract() || properties.getExtractAbstractMethods()) // extract abstract methods
                && (!method.isStatic() || properties.getExtractStaticMethods()) // extract static methods
                && (modifier != NO_MODIFIER || properties.getExtractDefaultMethods()) // extract default methods
                && (modifier != PROTECTED || properties.getExtractProtectedMethods()) // extract protected methods
                && (modifier != PRIVATE || properties.getExtractPrivateMethods())) { // extract private methods
            return true;
        } else if (method.isConstructor()) {
            report("Constructor");
        } else {
            report("Method");
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
        report("Package");
        return false;
    }

    /**
     * Checks whether a extracted type may be generated.
     * @param type is the extracted type.
     * @return true if it may be generated.
     */
    public boolean allowsGenerating(ExtractedType type) {
        if (type.isSelected() && (!type.isInnerType() || properties.getExtractNestedTypes())) {
            return true;
        }
        report(type.getClass().getSimpleName().substring(9)); // Class, Interface, Enumeration
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
            for (String className : reportMap.keySet()) {
                logger.info("   " + className + ": " + reportMap.get(className));
            }
        }
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