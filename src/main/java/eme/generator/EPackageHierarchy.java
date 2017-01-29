package eme.generator;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;

/**
 * This class allows to build a package structure, a package hierarchy from the package paths of .
 * @author Timur Saglam
 */
public class EPackageHierarchy {
    /**
     * Creates new empty {@link EPackage} from name and super package only.
     */
    protected static EPackage generatePackage(String name, EPackage superPackage) {
        EPackage ePackage = EcoreFactory.eINSTANCE.createEPackage();
        ePackage.setName(name);
        ePackage.setNsPrefix(name);
        ePackage.setNsURI(superPackage.getNsURI() + "." + name); // Set URI
        superPackage.getESubpackages().add(ePackage); // add to the super package
        return ePackage;
    }

    private final EPackage basePackage;

    /**
     * Basic constructor.
     * @param basePackage is the base {@link EPackage} of hierarchy.
     */
    public EPackageHierarchy(EPackage basePackage) {
        this.basePackage = basePackage;
    }

    /**
     * Adds an {@link EClassifier} to the package hierarchy. Generates the missing packages for the hierarchy.
     * @param classifier is the new {@link EClassifier}.
     * @param fullName is the full name of the classifier, including the packages. This is used to build the hierarchy.
     */
    public void add(EClassifier classifier, String fullName) {
        String[] path = packagePath(fullName); // get packages from name
        EPackage currentPackage = basePackage; // package pointer for traversing packages
        for (int i = 0; i < path.length; i++) {  // for every package in path
            currentPackage = getSubpackage(path[i], currentPackage); // traverse through hierarchy
        }
        currentPackage.getEClassifiers().add(classifier); // add data type
    }

    /**
     * Sorts the content of the package hierarchy.
     */
    public void sort() {
        sort(basePackage);
    }

    /**
     * Extracts the package path from an name.
     */
    private String[] packagePath(String name) {
        if (name.contains(".")) { // if has package path
            return name.substring(0, name.lastIndexOf('.')).split("\\."); // get path
        } else {
            return new String[] {}; // no package.
        }
    }

    /**
     * Sorts a list of {@link ENamedElement}s. The Interface {@link ENamedElement} does not implement the Interface
     * {@link Comparable}.
     */
    private <T extends ENamedElement> void sort(EList<T> list) {
        Map<String, T> elementMap = new HashMap<String, T>();
        for (T element : list) { // for every classifier:
            elementMap.put(element.getName(), element); // map with its name as key
        }
        list.clear(); // clear original list
        List<String> elementNames = new LinkedList<String>(elementMap.keySet()); // add names to list
        Collections.sort(elementNames, String.CASE_INSENSITIVE_ORDER); // sort names
        for (String name : elementNames) { // in sorted order
            list.add(elementMap.get(name)); // add classifiers from map to original list
        }
    }

    /**
     * Recursive sort method. See <code>DataTypeHierarchy.sort()</code>.
     */
    private void sort(EPackage ePackage) {
        sort(ePackage.getEClassifiers()); // sort classifiers
        sort(ePackage.getESubpackages()); // sort packages
        for (EPackage subpackage : ePackage.getESubpackages()) {
            sort(subpackage); // recursive call for every subpackage
        }
    }

    /**
     * Returns a specific sub package of an {@link EPackage}. Creates a new one from the package path if it does not
     * exist.
     */
    protected EPackage getSubpackage(String name, EPackage superPackage) {
        for (EPackage subpackage : superPackage.getESubpackages()) { // for all subpackages
            if (name.equals(subpackage.getName())) { // check if it is the wanted package
                return subpackage;
            }
        } // if wanted package does not exist:
        return generatePackage(name, superPackage); // create new
    }
}