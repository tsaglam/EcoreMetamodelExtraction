package eme.generator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;

import eme.model.ExtractedClass;
import eme.model.ExtractedEnumeration;
import eme.model.ExtractedInterface;
import eme.model.ExtractedMethod;
import eme.model.ExtractedPackage;
import eme.model.ExtractedType;
import eme.model.IntermediateModel;
import eme.model.datatypes.ExtractedAttribute;
import eme.model.datatypes.ExtractedDataType;
import eme.model.datatypes.ExtractedParameter;
import eme.parser.JavaProjectParser;
import eme.properties.ExtractionProperties;

/**
 * This class allows to generate Ecore metamodel components, which are EObjects, with simple method calls. It utilizes
 * the EcoreFactory class. The general order to call the methods is: 1. prepareFor(), 2. the generateEPackage(), 3.
 * completeGenration().
 * @author Timur Saglam
 */
public class EObjectGenerator {
    private static final Logger logger = LogManager.getLogger(JavaProjectParser.class.getName());
    private final Map<String, EClassifier> createdEClassifiers;
    private final EcoreFactory ecoreFactory;
    private final Map<EClass, ExtractedType> incompleteEClasses;
    private IntermediateModel model;
    private final ExtractionProperties properties;
    private final SelectionHelper selector;
    private final EDataTypeGenerator typeGenerator;

    /**
     * Basic constructor.
     * @param properties is the properties class for the extraction.
     */
    public EObjectGenerator(ExtractionProperties properties) {
        this.properties = properties;
        ecoreFactory = EcoreFactory.eINSTANCE;
        createdEClassifiers = new HashMap<String, EClassifier>();
        incompleteEClasses = new HashMap<EClass, ExtractedType>();
        typeGenerator = new EDataTypeGenerator(createdEClassifiers);
        selector = new SelectionHelper(properties);
    }

    /**
     * This method finishes the generation of the EObjects by adding methods and Attributes after the classes were
     * build. This separate part of the generation is necessary to avoid cyclic data type dependencies on ungenerated
     * classes. It also loggs some reports.
     */
    public void completeGeneration() {
        for (EClass eClass : incompleteEClasses.keySet()) { // for every generated EClass
            addOperations(incompleteEClasses.get(eClass), eClass.getEOperations(), eClass); // add methods
            addAttributes(incompleteEClasses.get(eClass), eClass.getEStructuralFeatures(), eClass); // add attributes
        }
        selector.generateReport(); // print reports
    }

    /**
     * Generates a EClassifier from an ExtractedType, if the type was not already generated.
     * @param type is the ExtractedType.
     * @return the EClassifier, which is either an EClass, an EInterface or an EEnum.
     */
    public EClassifier generateEClassifier(ExtractedType type) {
        String fullName = type.getFullName();
        if (createdEClassifiers.containsKey(fullName)) { // if already created:
            return createdEClassifiers.get(fullName); // just return from map.
        }
        EClassifier eClassifier = null;
        if (type.getClass() == ExtractedInterface.class) { // build interface:
            eClassifier = generateEClass(type, true, true);
        } else if (type.getClass() == ExtractedClass.class) { // build class:
            EClass eClass = generateEClass(type, ((ExtractedClass) type).isAbstract(), false);
            addSuperClass((ExtractedClass) type, eClass.getESuperTypes()); // get superclass
            eClassifier = eClass;
        } else if (type.getClass() == ExtractedEnumeration.class) { // build enum:
            eClassifier = generateEEnum((ExtractedEnumeration) type);
        }
        typeGenerator.addTypeParameters(eClassifier, type); // add generic types.
        eClassifier.setName(type.getName()); // set name
        createdEClassifiers.put(fullName, eClassifier); // store created classifier
        return eClassifier;
    }

    /**
     * Generates an EPackage from an extractedPackage. Recursively calls this method to all contained elements.
     * @param extractedPackage is the package the EPackage gets generated from.
     * @return the generated EPackage.
     */
    public EPackage generateEPackage(ExtractedPackage extractedPackage) {
        EPackage ePackage = ecoreFactory.createEPackage();
        if (extractedPackage.isRoot()) { // set root name & prefix:
            typeGenerator.setRoot(ePackage); // give type generator the root
            ePackage.setName(properties.getDefaultPackageName());
            ePackage.setNsPrefix(properties.getDefaultPackageName());
        } else { // set name & prefix for non root packages:
            ePackage.setName(extractedPackage.getName());
            ePackage.setNsPrefix(extractedPackage.getName());
        }
        ePackage.setNsURI(model.getProjectName() + "/" + extractedPackage.getFullName()); // Set URI
        for (ExtractedPackage subpackage : extractedPackage.getSubpackages()) { // for all packages
            if (selector.allowsGenerating(subpackage)) { // if is allowed to
                ePackage.getESubpackages().add(generateEPackage(subpackage)); // extract
            }
        }
        for (ExtractedType type : extractedPackage.getTypes()) { // for all types
            if (selector.allowsGenerating(type)) { // if is allowed to
                ePackage.getEClassifiers().add(generateEClassifier(type)); // extract
            }
        }
        return ePackage;
    }

    /**
     * Clears the caches of the class and sets the intermediate model.
     * @param model is the new model.
     */
    public void prepareFor(IntermediateModel model) {
        createdEClassifiers.clear(); // clear created classifiers
        incompleteEClasses.clear(); // clear unfinished classes.
        typeGenerator.reset(); // reset type generator to avoid duplicate data types.
        this.model = model; // set model
    }

    /**
     * Adds the attributes of an extracted type to a specific List of EStructuralFeatures.
     */
    private void addAttributes(ExtractedType type, List<EStructuralFeature> list, EClassifier classifier) {
        EAttribute eAttribute;
        for (ExtractedAttribute attribute : type.getAttributes()) { // for every attribute
            if (selector.allowsGenerating(attribute)) { // if should be generated:
                eAttribute = ecoreFactory.createEAttribute(); // create object
                eAttribute.setName(attribute.getIdentifier()); // set name
                eAttribute.setChangeable(!attribute.isFinal()); // make unchangeable if final
                if (typeGenerator.isTypeParameter(attribute, classifier)) {
                    eAttribute.setEGenericType(typeGenerator.generateGeneric(attribute, classifier));
                } else {
                    eAttribute.setEType(typeGenerator.generate(attribute)); // generate data type
                }
                typeGenerator.addGenericArguments(eAttribute.getEGenericType(), attribute, classifier); // add generic
                                                                                                        // arguments
                list.add(eAttribute);
            }
        }
    }

    /**
     * Adds the operations of an extracted type to a specific List of EOperations.
     */
    private void addOperations(ExtractedType type, List<EOperation> list, EClassifier classifier) {
        EOperation operation;
        for (ExtractedMethod method : type.getMethods()) { // for every method
            if (selector.allowsGenerating(method)) { // if should be generated.
                operation = ecoreFactory.createEOperation(); // create object
                operation.setName(method.getName()); // set name
                if (method.getReturnType() != null) { // build return type:
                    ExtractedDataType returnType = method.getReturnType();
                    if (typeGenerator.isTypeParameter(returnType, classifier)) {
                        operation.setEGenericType(typeGenerator.generateGeneric(returnType, classifier));
                    } else {
                        operation.setEType(typeGenerator.generate(returnType)); // generate data type
                    }
                    typeGenerator.addGenericArguments(operation.getEGenericType(), method.getReturnType(), classifier);
                }
                for (ExtractedDataType exception : method.getThrowsDeclarations()) {
                    if (typeGenerator.isTypeParameter(exception, classifier)) {
                        operation.getEGenericExceptions().add(typeGenerator.generateGeneric(exception, classifier));
                    } else {
                        operation.getEExceptions().add(typeGenerator.generate(exception)); // generate data type
                    }

                }
                addParameters(method, operation.getEParameters(), classifier); // add parameters
                list.add(operation);
            }
        }
    }

    /**
     * Adds the parameters of an extracted method to a specific List of EParameters.
     */
    private void addParameters(ExtractedMethod method, List<EParameter> list, EClassifier classifier) {
        EParameter eParameter;
        for (ExtractedParameter parameter : method.getParameters()) { // for every parameter
            eParameter = ecoreFactory.createEParameter(); // TODO (HIGH) solution for arrays.
            eParameter.setName(parameter.getIdentifier()); // set identifier
            if (typeGenerator.isTypeParameter(parameter, classifier)) {
                eParameter.setEGenericType(typeGenerator.generateGeneric(parameter, classifier));
            } else {
                eParameter.setEType(typeGenerator.generate(parameter)); // generate data type
            }
            typeGenerator.addGenericArguments(eParameter.getEGenericType(), parameter, classifier); // add generic
                                                                                                    // arguments
            list.add(eParameter);
        }
    }

    /**
     * Adds the super class of an extracted class to a specific List of EClasses. If the extracted class has no super
     * class, no EClass is added.
     */
    private void addSuperClass(ExtractedClass extractedClass, List<EClass> toList) {
        String superClassName = extractedClass.getSuperClass(); // super class name
        if (superClassName != null) { // if actually has super type
            if (createdEClassifiers.containsKey(superClassName)) { // if is already created:
                toList.add((EClass) createdEClassifiers.get(superClassName)); // get from map.
            } else { // if not already created:
                try { // create from type if found in model.
                    toList.add((EClass) generateEClassifier(model.getType(superClassName)));
                } catch (IllegalArgumentException exception) { // exception if it is external
                    logger.warn("Could not use external type as super class: " + superClassName);
                } // TODO (HIGH) external super classes.
            }
        }
    }

    /**
     * Adds all super interfaces of an extracted type to a specific List of EClasses. If the extracted type has no super
     * interfaces, no EClass is added.
     */
    private void addSuperInterfaces(ExtractedType type, List<EClass> toList) {
        for (String interfaceName : type.getSuperInterfaces()) { // for all interfaces
            if (createdEClassifiers.containsKey(interfaceName)) { // if already created
                toList.add((EClass) createdEClassifiers.get(interfaceName)); // add
            } else {
                try { // if not already created, try to create with type from model
                    toList.add((EClass) generateEClassifier(model.getType(interfaceName)));
                } catch (IllegalArgumentException exception) {  // exception if it is external
                    logger.warn("Could not use external type as super interface: " + interfaceName);
                } // TODO (HIGH) external super interfaces.
            }
        }
    }

    /**
     * Generates an EClass from an extractedType (should be ExtractedClass or ExtractedInterface).
     */
    private EClass generateEClass(ExtractedType extractedType, boolean isAbstract, boolean isInterface) {
        EClass eClass = ecoreFactory.createEClass(); // build object
        eClass.setAbstract(isAbstract); // set abstract or not
        eClass.setInterface(isInterface); // set interface or not
        addSuperInterfaces(extractedType, eClass.getESuperTypes()); // add super interfaces
        incompleteEClasses.put(eClass, extractedType); // finish building later
        return eClass;
    }

    /**
     * Generates an EEnum from an ExtractedEnumeration.
     */
    private EEnum generateEEnum(ExtractedEnumeration extractedEnum) {
        EEnum eEnum = ecoreFactory.createEEnum(); // create EEnum
        for (String enumeral : extractedEnum.getEnumerals()) { // for very Enumeral
            EEnumLiteral literal = ecoreFactory.createEEnumLiteral(); // create literal
            literal.setName(enumeral); // set name.
            literal.setValue(eEnum.getELiterals().size()); // set ordinal.
            eEnum.getELiterals().add(literal); // add literal to enum.
        }
        return eEnum;
    }
}