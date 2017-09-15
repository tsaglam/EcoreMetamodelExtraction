package eme;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EPackage;

/**
 * Temporary class for counting elements of Ecore metamodels.
 * @author Timur Saglam
 */
public final class MetamodelAnalyzer { // TODO (MEDIUM) optimize code, avoid redundancy.
    private static final Logger logger = LogManager.getLogger(MetamodelAnalyzer.class.getName());

    private MetamodelAnalyzer() {
        // private constructor.
    }

    /**
     * Analyzes an Ecore metamodel and logs the number of components.
     * @param eRoot is the root {@link EPackage} of the metamodel.
     */
    public static void analyze(EPackage eRoot) {
        logger.info("countPackages " + (1 + countPackages(eRoot)));
        logger.info("countClassifers " + countClassifers(eRoot));
        logger.info("  countClasses " + count(eRoot, EClass.class));
        logger.info("  countDataTypes " + count(eRoot, EDataType.class));
        logger.info("    countEnums " + count(eRoot, EEnum.class));
        logger.info("countOperations " + countOperations(eRoot));
        logger.info("countStrucutralFeatures " + countStrucutralFeatures(eRoot));
        logger.info("  countAttributes " + countAttributes(eRoot));
        logger.info("  countReferences " + countReferences(eRoot));
    }

    private static int count(EPackage current, Class<? extends ENamedElement> clazz) {
        int result = 0;
        for (EClassifier classifier : current.getEClassifiers()) {
            if (clazz.isInstance(classifier)) {
                result++;
            }
        }
        for (EPackage sub : current.getESubpackages()) {
            result += count(sub, clazz);
        }
        return result;
    }

    private static int countAttributes(EPackage current) {
        int result = 0;
        for (EClassifier classifier : current.getEClassifiers()) {
            if (classifier instanceof EClass) {
                result += ((EClass) classifier).getEAttributes().size();
            }
        }
        for (EPackage sub : current.getESubpackages()) {
            result += countAttributes(sub);
        }
        return result;
    }

    private static int countClassifers(EPackage current) {
        int result = current.getEClassifiers().size();
        for (EPackage sub : current.getESubpackages()) {
            result += countClassifers(sub);
        }
        return result;
    }

    private static int countOperations(EPackage current) {
        int result = 0;
        for (EClassifier classifier : current.getEClassifiers()) {
            if (classifier instanceof EClass) {
                result += ((EClass) classifier).getEOperations().size();
            }
        }
        for (EPackage sub : current.getESubpackages()) {
            result += countOperations(sub);
        }
        return result;
    }

    private static int countPackages(EPackage current) {
        int result = current.getESubpackages().size();
        for (EPackage sub : current.getESubpackages()) {
            result += countPackages(sub);
        }
        return result;
    }

    private static int countReferences(EPackage current) {
        int result = 0;
        for (EClassifier classifier : current.getEClassifiers()) {
            if (classifier instanceof EClass) {
                result += ((EClass) classifier).getEReferences().size();
            }
        }
        for (EPackage sub : current.getESubpackages()) {
            result += countReferences(sub);
        }
        return result;
    }

    private static int countStrucutralFeatures(EPackage current) {
        int result = 0;
        for (EClassifier classifier : current.getEClassifiers()) {
            if (classifier instanceof EClass) {
                result += ((EClass) classifier).getEStructuralFeatures().size();
            }
        }
        for (EPackage sub : current.getESubpackages()) {
            result += countStrucutralFeatures(sub);
        }
        return result;
    }
}