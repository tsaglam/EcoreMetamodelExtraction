package eme.generator;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

/**
 * This class allows to generate Ecore metamodel components with simple method calls. It utilizes
 * the EcoreFactory class.
 * @author Timur Saglam
 */
public class EcoreMetamodelGenerator {
    private EcoreFactory ecoreFactory;
    private boolean printResults;

    /**
     * Simple constructor, retrieves the instance of the EcoreFactory.
     * @param printResults determines whether the generator prints the products it creates.
     */
    public EcoreMetamodelGenerator(boolean printResults) {
        this.printResults = printResults;
        ecoreFactory = EcoreFactory.eINSTANCE;
    }

    /**
     * Creates an EAttribute with a specified name and EDataType.
     * @param name is the name of the EAttribute.
     * @param type is the EDataType of the EAttribute.
     * @return the created EAttribute.
     */
    public EAttribute createAttribute(String name, EDataType type) {
        check(name);
        EAttribute eAttribute = ecoreFactory.createEAttribute();
        eAttribute.setName(name);
        eAttribute.setEType(type);
        print(eAttribute);
        return eAttribute;
    }

    /**
     * Creates an EClass.
     * @param name is the name of the EClass.
     * @param isAbstract determines whether the class is abstract.
     * @param isInterface determines whether the class is an interface.
     * @return the created EClass.
     */
    public EClass createClass(String name, boolean isAbstract, boolean isInterface) {
        check(name);
        EClass eClass = ecoreFactory.createEClass();
        eClass.setName(name);
        eClass.setAbstract(isAbstract);
        eClass.setInterface(isInterface);
        print(eClass);
        return eClass;
    }

    /**
     * Creates an EPackage.
     * @param name is the name of the EPackage.
     * @param prefix is the preferred XMLNS prefix for the namespace URI of the EPackage.
     * @param uri is the namespace URI, a universally unique identification of the EPackage.
     * @return the created EPackage.
     */
    public EPackage createPackage(String name, String prefix, String uri) {
        check(name, prefix, uri);
        EPackage ePackage = ecoreFactory.createEPackage();
        ePackage.setName(name);
        ePackage.setNsPrefix(prefix);
        ePackage.setNsURI(uri);
        return ePackage;
    }

    /**
     * Creates an EReference from an EClass to another EClass.
     * @param name is the name of the reference.
     * @param from is the class which has the reference.
     * @param to is the class the reference refers to.
     * @return the created ERefrence, which is already added to the EClass from.
     */
    public EReference createReference(String name, EClass from, EClass to) {
        check(from, to);
        check(name);
        EReference eReference = ecoreFactory.createEReference();
        eReference.setName(name);
        eReference.setContainment(true); // every EObject must have a Container
        eReference.setEType(to); // type of the reference
        from.getEStructuralFeatures().add(eReference); // add reference to from eclass
        print(eReference);
        return eReference;
    }

    /**
     * Creates an Ecore file from an EPackage.
     * @param ePackage is the EPackage to save to.
     * @param ecoreFilePath is the file path where the Ecore file is saved.
     * @param ecoreFileName is the name of the newly created Ecore file.
     */
    public void savePackage(EPackage ePackage, String ecoreFilePath, String ecoreFileName) {
        check(ePackage);
        check(ecoreFilePath, ecoreFileName);
        ePackage.eClass(); // Initialize the EPackage:
        Resource.Factory.Registry registry = Resource.Factory.Registry.INSTANCE;
        Map<String, Object> map = registry.getExtensionToFactoryMap();
        map.put(EcorePackage.eNAME, new XMIResourceFactoryImpl());  // add default extension
        ResourceSet resourceSet = new ResourceSetImpl(); // get new resource set
        Resource resource = null; // create a resource:
        try {
            resource = resourceSet.createResource(URI.createFileURI(ecoreFilePath + ecoreFileName + ".ecore"));
        } catch (IllegalArgumentException exception) {
            exception.printStackTrace();
        }
        resource.getContents().add(ePackage); // add the EPackage as root.
        try { // save the content:
            resource.save(Collections.EMPTY_MAP);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    // Prints a generated product if printing is enabled.
    private void print(EObject product) {
        if (printResults) {
            System.out.println("Generated: " + product.toString());
        }
    }

    // Checks EObject parameters on being null.
    private void check(EObject... parameters) {
        for (EObject parameter : parameters) {
            if (parameter == null) {
                throw new IllegalArgumentException("Parameter can't be null: " + parameter);
            }
        }
    }

    // Checks String parameters on being null or empty.
    private void check(String... parameters) {
        for (String parameter : parameters) {
            if (parameter == null) {
                throw new IllegalArgumentException("Parameter can't be null: " + parameter);
            } else if (parameter.isEmpty()) {
                throw new IllegalArgumentException("Parameter can't be empty: " + parameter);
            }
        }
    }
}
