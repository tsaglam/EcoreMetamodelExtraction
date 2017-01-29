package eme.generator;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.ETypeParameter;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;

import eme.model.ExtractedType;
import eme.model.IntermediateModel;
import eme.model.datatypes.ExtractedDataType;
import eme.model.datatypes.ExtractedTypeParameter;
import eme.model.datatypes.WildcardStatus;

/**
 * Generator class for the generation of Ecore data types ({@link EDataType})
 * @author Timur Saglam
 */
public class EDataTypeGenerator {
    private static final Logger logger = LogManager.getLogger(EDataTypeGenerator.class.getName());
    private final Map<String, EClassifier> eClassifierMap;
    private final ExternalTypeHierarchy typeHierarchy;
    private final EcoreFactory ecoreFactory;
    private final IntermediateModel model;
    private final Map<String, EDataType> typeMap;

    /**
     * Basic constructor, builds the type maps.
     * @param model is the {@link IntermediateModel}.
     * @param eClassifierMap is the list of created {@link EClassifier}s. This is needed to get custom data types.
     * @param typeHierarchy is the external type package hierarchy.
     */
    public EDataTypeGenerator(IntermediateModel model, Map<String, EClassifier> eClassifierMap, ExternalTypeHierarchy typeHierarchy) {
        this.model = model;
        this.eClassifierMap = eClassifierMap; // set classifier map.
        this.typeHierarchy = typeHierarchy;
        ecoreFactory = EcoreFactory.eINSTANCE; // get ecore factory.
        typeMap = new HashMap<String, EDataType>(); // create type map.
        fillMap(); // fill type map.
    }

    /**
     * Adds all generic arguments from an {@link ExtractedDataType} to an {@link EGenericType}. For all generic
     * arguments add their generic arguments recursively.
     * @param genericType is the generic type of an attribute, a parameter or a method.
     * @param dataType is the extracted data type, an attribute, a parameter or a return type.
     * @param classifier is the EClassifier which owns the generic type.
     */
    public void addGenericArguments(EGenericType genericType, ExtractedDataType dataType, EClassifier classifier) {
        for (ExtractedDataType genericArgument : dataType.getGenericArguments()) { // for every generic argument
            EGenericType eArgument = ecoreFactory.createEGenericType(); // create ETypeArgument as EGenericType
            if (genericArgument.isWildcard()) { // wildcard argument:
                addBound(eArgument, genericArgument);
            } else if (isTypeParameter(genericArgument, classifier)) { // type parameter argument:
                eArgument.setETypeParameter(findTypeParameter(genericArgument, classifier));
            } else { // normal generic argument:
                eArgument.setEClassifier(generate(genericArgument));
            }
            addGenericArguments(eArgument, genericArgument, classifier); // recursively add generic arguments
            genericType.getETypeArguments().add(eArgument); // add ETypeArgument to original generic type
        }
    }

    /**
     * Adds all generic type parameters from an {@link ExtractedType} to a {@link EClassifier}.
     * @param classifier is the classifier.
     * @param type is the extracted type.
     */
    public void addTypeParameters(EClassifier classifier, ExtractedType type) {
        ETypeParameter eTypeParameter; // ecore type parameter
        for (ExtractedTypeParameter typeParameter : type.getTypeParameters()) { // for all type parameters
            eTypeParameter = ecoreFactory.createETypeParameter(); // create object
            eTypeParameter.setName(typeParameter.getIdentifier()); // set name
            addBounds(eTypeParameter, typeParameter, classifier);
            classifier.getETypeParameters().add(eTypeParameter); // add type parameter to EClassifier
        }
    }

    /**
     * Returns an {@link EClassifier} for an {@link ExtractedDataType} that can be used as data type for methods and
     * attributes.
     * @param extractedDataType is the extracted data type.
     * @return the data type as {@link EClassifier}, which is either (1.) a custom class from the model, or (2.) or an
     * external class that has to be created as data type, or (3.) an already known data type (Basic type or already
     * created).
     */
    public EClassifier generate(ExtractedDataType extractedDataType) {
        EDataType eDataType;
        String fullName = extractedDataType.getFullType();
        if (eClassifierMap.containsKey(fullName)) { // if is custom class
            return eClassifierMap.get(fullName);
        } else if (typeMap.containsKey(fullName)) { // if is basic type or already known
            return typeMap.get(fullName); // access EDataType
        } else { // if its an external type
            eDataType = generateExternal(extractedDataType); // create new EDataType
            addTypeParameters(eDataType, extractedDataType); // try to guess type parameters
            typeHierarchy.add(eDataType);
            return eDataType;
        }
    }

    /**
     * Returns an generic type parameter, which is an {@link EGenericType}, for an {@link ExtractedDataType} that can be
     * used as generic argument for methods and attributes.
     * @param dataType is the ExtractedDataType.
     * @param eClass is the EClass that owns the the EGenericType
     * @return the EGenericType.
     */
    public EGenericType generateGeneric(ExtractedDataType dataType, EClass eClass) {
        if (isTypeParameter(dataType, eClass)) {
            EGenericType genericType = ecoreFactory.createEGenericType();
            genericType.setETypeParameter(findTypeParameter(dataType, eClass));
            return genericType;
        }
        throw new IllegalArgumentException("The data type is not an type parameter: " + dataType.toString());
    }

    /**
     * Checks whether an {@link ExtractedDataType} is a type parameter in a specific {@link EClassifier}.
     * @param dataType is the extracted data type.
     * @param classifier is the specific EClassifier.
     * @return true if the extracted data type is a type parameter.
     */
    public boolean isTypeParameter(ExtractedDataType dataType, EClassifier classifier) {
        String dataTypeName = dataType.getFullType();
        for (ETypeParameter parameter : classifier.getETypeParameters()) {
            if (parameter.getName() != null && parameter.getName().equals(dataTypeName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Adds a bound to an wild card argument if it has one.
     */
    private void addBound(EGenericType eArgument, ExtractedDataType genericArgument) {
        WildcardStatus status = genericArgument.getWildcardStatus(); // get wild card status
        if (status != WildcardStatus.WILDCARD) { // if has bounds:
            EGenericType bound = ecoreFactory.createEGenericType(); // create bound
            bound.setEClassifier(generate(genericArgument)); // generate bound type
            if (status == WildcardStatus.WILDCARD_LOWER_BOUND) {
                eArgument.setELowerBound(bound); // add lower bound
            } else {
                eArgument.setEUpperBound(bound); // add upper bound
            }
        }
    }

    /**
     * Adds all bounds of an {@link ExtractedTypeParameter} to a {@link ETypeParameter}.
     */
    private void addBounds(ETypeParameter eTypeParameter, ExtractedTypeParameter typeParameter, EClassifier classifier) {
        EGenericType eBound; // ecore type parameter bound
        for (ExtractedDataType bound : typeParameter.getBounds()) { // for all bounds+
            if (!Object.class.getName().equals(bound.getFullType())) { // ignore object bound
                eBound = ecoreFactory.createEGenericType(); // create object
                if (isTypeParameter(bound, classifier)) {
                    eBound.setETypeParameter(findTypeParameter(bound, classifier));
                } else {
                    eBound.setEClassifier(generate(bound));
                }
                addGenericArguments(eBound, bound, classifier); // add generic arguments of bound
                eTypeParameter.getEBounds().add(eBound); // add bound to type parameter
            }
        }
    }

    /**
     * Adds bland generic type arguments to an classifier through a extracted data type. This should only be used for
     * external data types because it does not extract the actual types of the type parameters.
     */
    private void addTypeParameters(EClassifier classifier, ExtractedDataType dataType) {
        String typeName = dataType.getFullType(); // get type name
        if (model.containsExternal(typeName)) { // if is external type in model
            addTypeParameters(classifier, model.getExternalType(typeName)); // add parameters from external model type
        } else if (!dataType.getGenericArguments().isEmpty()) { // if external type is unknown
            logger.error("Can not resolve type parameters for " + dataType.toString());
        }
    }

    private void fillMap() {
        typeMap.put("boolean", EcorePackage.eINSTANCE.getEBoolean());
        typeMap.put("byte", EcorePackage.eINSTANCE.getEByte());
        typeMap.put("char", EcorePackage.eINSTANCE.getEChar());
        typeMap.put("double", EcorePackage.eINSTANCE.getEDouble());
        typeMap.put("float", EcorePackage.eINSTANCE.getEFloat());
        typeMap.put("int", EcorePackage.eINSTANCE.getEInt());
        typeMap.put("long", EcorePackage.eINSTANCE.getELong());
        typeMap.put("short", EcorePackage.eINSTANCE.getEShort());
        typeMap.put("java.lang.Boolean", EcorePackage.eINSTANCE.getEBooleanObject());
        typeMap.put("java.lang.Byte", EcorePackage.eINSTANCE.getEByteObject());
        typeMap.put("java.lang.Character", EcorePackage.eINSTANCE.getECharacterObject());
        typeMap.put("java.lang.Double", EcorePackage.eINSTANCE.getEDoubleObject());
        typeMap.put("java.lang.Float", EcorePackage.eINSTANCE.getEFloatObject());
        typeMap.put("java.lang.Integer", EcorePackage.eINSTANCE.getEIntegerObject());
        typeMap.put("java.lang.Long", EcorePackage.eINSTANCE.getELongObject());
        typeMap.put("java.lang.Short", EcorePackage.eINSTANCE.getEShortObject());
        typeMap.put("java.lang.String", EcorePackage.eINSTANCE.getEString());
        typeMap.put("java.lang.Object", EcorePackage.eINSTANCE.getEJavaObject());
        typeMap.put("java.lang.Class", EcorePackage.eINSTANCE.getEJavaClass());
    }

    /**
     * Gets an ETypeParameter with a specific name from an specific EClassifier.
     */
    private ETypeParameter findTypeParameter(ExtractedDataType dataType, EClassifier classifier) {
        String name = dataType.getFullType();
        for (ETypeParameter parameter : classifier.getETypeParameters()) {
            if (parameter.getName().equals(name)) {
                return parameter;
            }
        }
        throw new IllegalArgumentException("There is no ETypeParameter " + name + " in " + classifier.toString());
    }

    /**
     * Creates a new EDataType from an ExtractedDataType. The new EDataType can then be accessed from the type map or
     * array type map.
     */
    private EDataType generateExternal(ExtractedDataType extractedDataType) {
        if (typeMap.containsKey(extractedDataType.getFullType())) { // if already created:
            throw new IllegalArgumentException("Can't create an already created data type."); // throw exception
        }
        EDataType newType = ecoreFactory.createEDataType(); // new data type.
        newType.setName(extractedDataType.getType()); // set name
        newType.setInstanceTypeName(extractedDataType.getFullType()); // set full name
        typeMap.put(extractedDataType.getFullType(), newType); // store in map for later use
        return newType;
    }
}