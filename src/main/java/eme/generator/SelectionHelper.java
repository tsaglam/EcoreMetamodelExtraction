package eme.generator;

import static eme.model.datatypes.AccessLevelModifier.PRIVATE;
import static eme.model.datatypes.AccessLevelModifier.PROTECTED;
import static eme.model.datatypes.AccessLevelModifier.PUBLIC;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import eme.model.ExtractedMethod;
import eme.model.ExtractedPackage;
import eme.model.ExtractedType;
import eme.model.datatypes.AccessLevelModifier;
import eme.model.datatypes.ExtractedAttribute;
import eme.properties.ExtractionProperties;

/**
 * This class helps to decide whether a extracted element may be generated or not. It combines rules from a properties
 * class with the selection states of the model elements themselves.
 * @author Timur Saglam
 */
public class SelectionHelper {
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
                && (modifier != PROTECTED || properties.getExtractProtectedAttributes())
                && (modifier != PRIVATE || properties.getExtractPrivateAttributes())) {
            return true;
        }
        report(attribute);
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
                && (!method.isStatic() || properties.getExtractStaticMethods())// extract static methods
                && (modifier != PROTECTED || properties.getExtractProtectedMethods()) // extract protected methods
                && (modifier != PRIVATE || properties.getExtractPrivateMethods())) {
            return true;
        }
        report(method);
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
        report(subpackage);
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
        report(type);
        return false;
    }

    /**
     * Generates a report about the ungenerated elements.
     * @return the report.
     */
    public List<String> getReport() {
        List<String> reports = new LinkedList<String>();
        if (reportMap.isEmpty()) {
            reports.add("There were no ungenerated elements.");
        } else {
            reports.add("There were ungenerated elements because of selection and/or properties:");
            for (String className : reportMap.keySet()) {
                reports.add("   " + className + ": " + reportMap.get(className));
            }
        }
        return reports;
    }

    /**
     * Increases the number of ungenerated elements for a specific type of elements.
     * @param element is the specific type of elements.
     */
    private void report(Object element) {
        String className = element.getClass().getSimpleName();
        if (reportMap.containsKey(className)) {
            reportMap.put(className, reportMap.get(className) + 1);
        } else {
            reportMap.put(className, 1);
        }
    }
}