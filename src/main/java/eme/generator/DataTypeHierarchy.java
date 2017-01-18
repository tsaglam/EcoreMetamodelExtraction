package eme.generator;

import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EPackage;

import eme.model.ExtractedPackage;

/**
 * This class allows to build a package structure from a list of EDataTypes.
 * @author Timur Saglam
 */
public class DataTypeHierarchy {
    private final EPackage basePackage;
    private final String basePath;
    private final EObjectGenerator eObjectGenerator;

    /**
     * Simple constructor, builds the base for the hierarchy.
     * @param eObjectGenerator is the eObjectGenerator that uses this hierarchy.
     * @param basePackage is the base package for the hierarchy.
     */
    public DataTypeHierarchy(EObjectGenerator eObjectGenerator, EPackage basePackage, String basePath) {
        this.eObjectGenerator = eObjectGenerator;
        this.basePackage = basePackage;
        this.basePath = basePath;
    }

    /**
     * TODO comment
     * @param dataType
     */
    public void add(EDataType dataType) {
        String[] path = packagePath(dataType.getInstanceTypeName()); // get packages from name
        EPackage currentPackage = basePackage; // package pointer for traversing packages
        String fullName = "";
        for (int i = 0; i < path.length; i++) {  // for every package in path
            fullName += path[i]; // add next package to full name
            currentPackage = getSubpackage(path[i], fullName, currentPackage); // traverse through hierarchy
            fullName += "."; // add separator to full name
        }
        currentPackage.getEClassifiers().add(dataType); // add data type
    }

    /**
     * Returns a specific sub package of an EPackage. Creates a new one from the package path if it does not exist.
     */
    private EPackage getSubpackage(String name, String fullName, EPackage superPackage) {
        for (EPackage subpackage : superPackage.getESubpackages()) {
            if (name.equals(subpackage.getName())) {
                return subpackage;
            }
        }

        EPackage ePackage = eObjectGenerator.generateEPackage(new ExtractedPackage(basePath + "." + fullName));
        superPackage.getESubpackages().add(ePackage);
        return ePackage;
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

}
