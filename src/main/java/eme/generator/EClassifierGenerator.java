package eme.generator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;

import eme.model.ExtractedClass;
import eme.model.ExtractedEnumeration;
import eme.model.ExtractedInterface;
import eme.model.ExtractedType;
import eme.model.IntermediateModel;
import eme.model.datatypes.ExtractedAttribute;

/**
 * Generator class for Ecore classifiers ({@link EClassifier}s).
 * @author Timur Saglam
 */
public class EClassifierGenerator {
    private static final Logger logger = LogManager.getLogger(EClassifierGenerator.class.getName());
    private final Map<String, EClassifier> eClassifierMap;
    private final EcoreFactory ecoreFactory;
    private final ExternalTypeHierarchy externalTypes;
    private final Map<EClass, ExtractedType> bareEClasses;
    private final IntermediateModel model;
    private final EOperationGenerator operationGenerator;
    private final SelectionHelper selector;
    private final EDataTypeGenerator typeGenerator;

    /**
     * Basic constructor.
     * @param model is the {@link IntermediateModel} which is used to extract a metamodel.
     * @param root is the root {@link EPackage} of the metamodel.
     * @param selector is the {@link SelectionHelper} instance.
     */
    public EClassifierGenerator(IntermediateModel model, EPackage root, SelectionHelper selector) {
        this.model = model;
        this.selector = selector;
        ecoreFactory = EcoreFactory.eINSTANCE;
        eClassifierMap = new HashMap<String, EClassifier>();
        bareEClasses = new HashMap<EClass, ExtractedType>();
        externalTypes = new ExternalTypeHierarchy(root, selector.getProperties());
        typeGenerator = new EDataTypeGenerator(model, eClassifierMap, externalTypes);
        operationGenerator = new EOperationGenerator(typeGenerator, selector);
    }

    /**
     * Completes the generation of the {@link EClassifier} objects. Adds methods and attributes to {@link EClass}
     * objects and sorts the external types.
     */
    public void completeEClassifiers() {
        for (EClass eClass : bareEClasses.keySet()) { // for every generated EClass
            addAttributes(bareEClasses.get(eClass), eClass); // add attributes
            operationGenerator.addOperations(bareEClasses.get(eClass), eClass); // add methods
        }
        externalTypes.sort();
    }

    /**
     * Generates an {@link EClassifier} from an ExtractedType, if the type was not already generated.
     * @param type is the {@link ExtractedType}.
     * @return the {@link EClassifier}, which is either an {@link EClass}, an {@link ExtractedInterface} or an
     * {@link EEnum}.
     */
    public EClassifier generateEClassifier(ExtractedType type) {
        String fullName = type.getFullName();
        if (eClassifierMap.containsKey(fullName)) { // if already created:
            return eClassifierMap.get(fullName); // just return from map.
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
        eClassifierMap.put(fullName, eClassifier); // store created classifier
        return eClassifier;
    }

    /**
     * Adds the attributes of an extracted type to a specific List of EStructuralFeatures.
     */
    private void addAttributes(ExtractedType extractedType, EClass eClass) {
        for (ExtractedAttribute attribute : extractedType.getAttributes()) { // for every attribute
            if (selector.allowsGenerating(attribute)) { // if should be generated:
                if (isEClass(attribute.getFullType())) { // if type is EClass:
                    EReference reference = ecoreFactory.createEReference();
                    reference.setContainment(true); // has to be contained
                    addStructuralFeature(reference, attribute, eClass); // build reference
                } else { // if it is EDataType:
                    addStructuralFeature(ecoreFactory.createEAttribute(), attribute, eClass); // build attribute
                }
            }
        }
    }

    /**
     * Builds a structural feature from an extracted attribute and adds it to an EClass. A structural feature can be an
     * EAttribute or an EReference. If it is a reference, containment has to be set manually.
     */
    private void addStructuralFeature(EStructuralFeature feature, ExtractedAttribute attribute, EClass eClass) {
        feature.setName(attribute.getIdentifier()); // set name
        feature.setChangeable(!attribute.isFinal()); // make unchangeable if final
        if (typeGenerator.isTypeParameter(attribute, eClass)) { // if is generic
            feature.setEGenericType(typeGenerator.generateGeneric(attribute, eClass)); // generate generic
        } else {
            feature.setEType(typeGenerator.generate(attribute)); // generate data type
        }
        typeGenerator.addGenericArguments(feature.getEGenericType(), attribute, eClass); // add generic args
        eClass.getEStructuralFeatures().add(feature); // add feature to EClass
    }

    /**
     * Adds the super class of an extracted class to a specific List of EClasses. If the extracted class has no super
     * class, no EClass is added.
     */
    private void addSuperClass(ExtractedClass extractedClass, List<EClass> toList) {
        String superClassName = extractedClass.getSuperClass(); // super class name
        if (superClassName != null) { // if actually has super type
            if (eClassifierMap.containsKey(superClassName)) { // if is already created:
                toList.add((EClass) eClassifierMap.get(superClassName)); // get from map.
            } else if (model.contains(superClassName)) { // if is not created yet
                toList.add((EClass) generateEClassifier(model.getType(superClassName))); // create
            } else { // else: is external type
                logger.warn("Could not use external type as super class: " + superClassName);
            }
        }
    }

    /**
     * Adds all super interfaces of an extracted type to a specific List of EClasses. If the extracted type has no super
     * interfaces, no EClass is added.
     */
    private void addSuperInterfaces(ExtractedType type, List<EClass> toList) {
        for (String interfaceName : type.getSuperInterfaces()) { // for all interfaces
            if (eClassifierMap.containsKey(interfaceName)) { // if already created
                toList.add((EClass) eClassifierMap.get(interfaceName)); // add
            } else if (model.contains(interfaceName)) { // if is not created yet
                toList.add((EClass) generateEClassifier(model.getType(interfaceName))); // create
            } else { // else: is external type
                logger.warn("Could not use external type as super interface: " + interfaceName);
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
        bareEClasses.put(eClass, extractedType); // finish building later
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

    /**
     * Checks whether a specific type name is an already created EClass.
     */
    private boolean isEClass(String typeName) {
        return eClassifierMap.containsKey(typeName) && !(eClassifierMap.get(typeName) instanceof EEnum);
    }
}