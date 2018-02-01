package eme.generator;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.ETypeParameter;
import org.eclipse.emf.ecore.ETypedElement;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;

import eme.generator.hierarchies.ExternalTypeHierarchy;
import eme.model.ExtractedMethod;
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
    private final Map<String, EDataType> dataTypeMap;
    private final Map<String, EClassifier> eClassifierMap;
    private final EcoreFactory ecoreFactory;
    private final IntermediateModel model;
    private final ExternalTypeHierarchy typeHierarchy;

    /**
     * Basic constructor, builds the type maps.
     * @param model is the {@link IntermediateModel}.
     * @param eClassifierMap is the list of created {@link EClassifier}s. This is needed to get custom data types.
     * @param typeHierarchy is the external type package hierarchy.
     */
    public EDataTypeGenerator(IntermediateModel model, Map<String, EClassifier> eClassifierMap, ExternalTypeHierarchy typeHierarchy) {
        this.model = model;
        this.eClassifierMap = eClassifierMap; // set eClassifier map.
        this.typeHierarchy = typeHierarchy;
        ecoreFactory = EcoreFactory.eINSTANCE; // get ecore factory.
        dataTypeMap = new HashMap<String, EDataType>(); // create type map.
        fillMap(); // fill type map.
    }

    /**
     * Adds data type (Either {@link EDataType} or {@link EGenericType}) to an {@link ETypedElement} from an
     * {@link ExtractedDataType}.
     * @param element is the {@link ETypedElement}.
     * @param dataType is the {@link EDataType}.
     * @param source is the source of {@link ETypeParameter}s, an {@link TypeParameterSource}.
     */
    public void addDataType(ETypedElement element, ExtractedDataType dataType, TypeParameterSource source) {
        if (source.containsTypeParameter(dataType)) {
            element.setEGenericType(generateGeneric(dataType, source));
        } else {
            element.setEType(generate(dataType)); // generate data type
        }
        addGenericArguments(element.getEGenericType(), dataType, source); // add generic
    }

    /**
     * Adds exception (Either {@link EDataType} or {@link EGenericType}) to an {@link EOperation} from an
     * {@link ExtractedDataType}.
     * @param operation is the {@link EOperation}.
     * @param exception is the {@link ExtractedDataType}.
     * @param source is the source of {@link ETypeParameter}s, an {@link TypeParameterSource}.
     */
    public void addException(EOperation operation, ExtractedDataType exception, TypeParameterSource source) {
        if (source.containsTypeParameter(exception)) {
            operation.getEGenericExceptions().add(generateGeneric(exception, source));
        } else {
            operation.getEExceptions().add(generate(exception)); // generate data type
        }
    }

    /**
     * Adds all generic arguments from an {@link ExtractedDataType} to an {@link EGenericType}. For all generic
     * arguments add their generic arguments recursively.
     * @param genericType is the generic type of an attribute, a parameter or a method.
     * @param dataType is the extracted data type, an attribute, a parameter or a return type.
     * @param source is the source of {@link ETypeParameter}s, an {@link TypeParameterSource}.
     */
    public void addGenericArguments(EGenericType genericType, ExtractedDataType dataType, TypeParameterSource source) {
        for (ExtractedDataType genericArgument : dataType.getGenericArguments()) { // for every generic argument
            EGenericType eArgument = ecoreFactory.createEGenericType(); // create ETypeArgument as EGenericType
            if (genericArgument.isWildcard()) { // wildcard argument:
                addWildcardBound(eArgument, genericArgument, source);
            } else { // normal argument or type parameter
                generateBoundType(eArgument, genericArgument, source);
            }
            addGenericArguments(eArgument, genericArgument, source); // recursively add generic arguments
            genericType.getETypeArguments().add(eArgument); // add ETypeArgument to original generic type
        }
    }

    /**
     * Adds all generic type parameters from an {@link ExtractedType} to a {@link EClassifier}.
     * @param eClassifier is the EClassifier.
     * @param type is the extracted type.
     */
    public void addTypeParameters(EClassifier eClassifier, ExtractedType type) {
        eClassifier.getETypeParameters().addAll(generateETypeParameters(type.getTypeParameters()));
        finishTypeParameters(eClassifier.getETypeParameters(), type.getTypeParameters(), new TypeParameterSource(eClassifier));
    }

    /**
     * Adds all generic type parameters from an {@link ExtractedMethod} to a {@link EOperation}.
     * @param eOperation is the {@link EOperation}.
     * @param method is the {@link ExtractedMethod}.
     */
    public void addTypeParameters(EOperation eOperation, ExtractedMethod method) {
        eOperation.getETypeParameters().addAll(generateETypeParameters(method.getTypeParameters()));
        finishTypeParameters(eOperation.getETypeParameters(), method.getTypeParameters(), new TypeParameterSource(eOperation));
    }

    /**
     * Adds all bounds of an {@link ExtractedTypeParameter} to a {@link ETypeParameter}.
     */
    private void addBounds(ETypeParameter eTypeParameter, ExtractedTypeParameter typeParameter, TypeParameterSource source) {
        EGenericType eBound; // ecore type parameter bound
        for (ExtractedDataType bound : typeParameter.getBounds()) { // for all bounds
            if (!Object.class.getName().equals(bound.getFullType())) { // ignore object bound
                eBound = ecoreFactory.createEGenericType(); // create object
                generateBoundType(eBound, bound, source); // set type of bound
                addGenericArguments(eBound, bound, source); // add generic arguments of bound
                eTypeParameter.getEBounds().add(eBound); // add bound to type parameter
            }
        }
    }

    /**
     * Adds a bound to an wild card argument if it has one.
     */
    private void addWildcardBound(EGenericType eArgument, ExtractedDataType genericArgument, TypeParameterSource source) {
        WildcardStatus status = genericArgument.getWildcardStatus(); // get wild card status
        if (status != WildcardStatus.UNBOUND) { // if has bounds:
            EGenericType bound = ecoreFactory.createEGenericType(); // create bound
            generateBoundType(bound, genericArgument, source); // generate bound type
            if (status == WildcardStatus.LOWER_BOUND) {
                eArgument.setELowerBound(bound); // add lower bound
            } else {
                eArgument.setEUpperBound(bound); // add upper bound
            }
        }
    }

    /**
     * Default data type map entries.
     */
    private void fillMap() {
        dataTypeMap.put("boolean", EcorePackage.eINSTANCE.getEBoolean());
        dataTypeMap.put("byte", EcorePackage.eINSTANCE.getEByte());
        dataTypeMap.put("char", EcorePackage.eINSTANCE.getEChar());
        dataTypeMap.put("double", EcorePackage.eINSTANCE.getEDouble());
        dataTypeMap.put("float", EcorePackage.eINSTANCE.getEFloat());
        dataTypeMap.put("int", EcorePackage.eINSTANCE.getEInt());
        dataTypeMap.put("long", EcorePackage.eINSTANCE.getELong());
        dataTypeMap.put("short", EcorePackage.eINSTANCE.getEShort());
        dataTypeMap.put("java.lang.Boolean", EcorePackage.eINSTANCE.getEBooleanObject());
        dataTypeMap.put("java.lang.Byte", EcorePackage.eINSTANCE.getEByteObject());
        dataTypeMap.put("java.lang.Character", EcorePackage.eINSTANCE.getECharacterObject());
        dataTypeMap.put("java.lang.Double", EcorePackage.eINSTANCE.getEDoubleObject());
        dataTypeMap.put("java.lang.Float", EcorePackage.eINSTANCE.getEFloatObject());
        dataTypeMap.put("java.lang.Integer", EcorePackage.eINSTANCE.getEIntegerObject());
        dataTypeMap.put("java.lang.Long", EcorePackage.eINSTANCE.getELongObject());
        dataTypeMap.put("java.lang.Short", EcorePackage.eINSTANCE.getEShortObject());
        dataTypeMap.put("java.lang.String", EcorePackage.eINSTANCE.getEString());
        dataTypeMap.put("java.lang.Object", EcorePackage.eINSTANCE.getEJavaObject());
        dataTypeMap.put("java.lang.Class", EcorePackage.eINSTANCE.getEJavaClass());
    }

    /**
     * Adds all bounds for every {@link ETypeParameter} of an {@link EClassifier}.
     */
    private void finishTypeParameters(List<ETypeParameter> eTypeParameters, List<ExtractedTypeParameter> typeParameters, TypeParameterSource source) {
        Iterator<ExtractedTypeParameter> iterator = typeParameters.iterator();
        Iterator<ETypeParameter> ecoreIterator = eTypeParameters.iterator();
        while (iterator.hasNext() && ecoreIterator.hasNext()) {
            addBounds(ecoreIterator.next(), iterator.next(), source);
        }
    }

    /**
     * Returns an {@link EClassifier} for an {@link ExtractedDataType} that can be used as data type for methods and
     * attributes. The {@link EClassifier} is either (1.) a custom class from the model, or (2.) or an external class
     * that has to be created as data type, or (3.) an already known data type (Basic type or already created)
     */
    private EClassifier generate(ExtractedDataType extractedDataType) {
        EDataType eDataType;
        String fullName = extractedDataType.getFullType();
        if (eClassifierMap.containsKey(fullName)) { // if is custom class
            return eClassifierMap.get(fullName);
        } else if (dataTypeMap.containsKey(fullName)) { // if is basic type or already known
            return dataTypeMap.get(fullName); // access EDataType
        } else { // if its an external type
            eDataType = generateExternalType(extractedDataType); // create new EDataType
            typeHierarchy.add(eDataType);
            return eDataType;
        }
    }

    /**
     * Sets the type of an {@link EGenericType} from a bound {@link ExtractedDataType}. This is either an
     * {@link ETypeParameter} if the {@link ExtractedDataType} is a type parameter in the {@link TypeParameterSource} or
     * {@link EClassifier} if not.
     */
    private void generateBoundType(EGenericType genericType, ExtractedDataType boundType, TypeParameterSource source) {
        if (source.containsTypeParameter(boundType)) {
            genericType.setETypeParameter(source.getTypeParameter(boundType));
        } else {
            genericType.setEClassifier(generate(boundType));
        }
    }

    /**
     * Generates list of {@link ETypeParameter}s from list of {@link ExtractedTypeParameter}s.
     */
    private List<ETypeParameter> generateETypeParameters(List<ExtractedTypeParameter> typeParameters) {
        ETypeParameter eTypeParameter; // ecore type parameter
        List<ETypeParameter> eTypeParameters = new LinkedList<ETypeParameter>();
        for (ExtractedTypeParameter typeParameter : typeParameters) { // for all type parameters
            eTypeParameter = ecoreFactory.createETypeParameter(); // create object
            eTypeParameter.setName(typeParameter.getIdentifier()); // set name
            eTypeParameters.add(eTypeParameter);
        }
        return eTypeParameters;
    }

    /**
     * Creates a new EDataType from an ExtractedDataType. The new EDataType can then be accessed from the type map or
     * array type map.
     */
    private EDataType generateExternalType(ExtractedDataType extractedDataType) {
        if (dataTypeMap.containsKey(extractedDataType.getFullType())) { // if already created:
            throw new IllegalArgumentException("Can't create an already created data type."); // throw exception
        }
        EDataType eDataType = ecoreFactory.createEDataType();
        eDataType.setName(extractedDataType.getType());
        eDataType.setInstanceTypeName(extractedDataType.getFullType()); // set full name
        String dataTypeName = extractedDataType.getFullArrayType(); // get type name without array brackets.
        if (model.containsExternal(dataTypeName)) {
            addTypeParameters(eDataType, model.getExternalType(dataTypeName)); // add parameters from external type
        } else if (!extractedDataType.getGenericArguments().isEmpty()) { // if external type is unknown
            logger.error("Can not resolve type parameters for " + extractedDataType.toString());
        }
        dataTypeMap.put(extractedDataType.getFullType(), eDataType); // store in map for later use
        return eDataType;
    }

    /**
     * Returns an generic type parameter, which is an {@link EGenericType}, for an {@link ExtractedDataType} that can be
     * used as generic argument for methods and attributes.
     */
    private EGenericType generateGeneric(ExtractedDataType dataType, TypeParameterSource source) {
        if (source.containsTypeParameter(dataType)) {
            EGenericType genericType = ecoreFactory.createEGenericType();
            genericType.setETypeParameter(source.getTypeParameter(dataType));
            return genericType;
        }
        throw new IllegalArgumentException("The data type is not an type parameter: " + dataType.toString());
    }
}