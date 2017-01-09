package eme.generator;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.ETypeParameter;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;

import eme.model.ExtractedType;
import eme.model.datatypes.ExtractedDataType;
import eme.model.datatypes.ExtractedTypeParameter;
import eme.model.datatypes.WildcardStatus;

/**
 * Generator class for the generation of Ecore data types: EDataTypes
 * @author Timur Saglam
 */
public class EDataTypeGenerator {
    private final Map<String, EClassifier> createdEClassifiers;
    private final EcoreFactory ecoreFactory;
    private EPackage root;
    private final Map<String, EDataType> typeMap;

    /**
     * Basic constructor, builds the type map.
     * @param createdEClassifiers is the list of created classifiers. This is needed to get custom data types.
     */
    public EDataTypeGenerator(Map<String, EClassifier> createdEClassifiers) {
        this.createdEClassifiers = createdEClassifiers; // set classifier map.
        ecoreFactory = EcoreFactory.eINSTANCE; // get ecore factory.
        typeMap = new HashMap<String, EDataType>(); // create type map.
        fillMap(); // fill type map.
    }

    /**
     * Adds all generic arguments from an extracted data type to an generic type. For all generic arguments add their
     * generic arguments recursively.
     * @param genericType is the generic type of an attribute, a parameter or a method.
     * @param dataType is the extracted data type, an attribute, a parameter or a return type.
     * @param classifier is the EClassifier which owns the generic type.
     */
    public void addGenericArguments(EGenericType genericType, ExtractedDataType dataType, EClassifier classifier) {
        for (ExtractedDataType genericArgument : dataType.getGenericArguments()) { // for every generic argument
            EGenericType eTypeArgument = ecoreFactory.createEGenericType(); // create ETypeArgument as EGenericType
            if (genericArgument.isWildcard()) {
                WildcardStatus status = genericArgument.getWildcardStatus();
                eTypeArgument.setETypeParameter(ecoreFactory.createETypeParameter());
                EGenericType bound = ecoreFactory.createEGenericType();
                if (status == WildcardStatus.WILDCARD_LOWER_BOUND) {
                    bound.setEClassifier(generate(genericArgument));
                    eTypeArgument.setELowerBound(bound);
                } else if (status == WildcardStatus.WILDCARD_UPPER_BOUND) { // TODO (HIGH) code quality & duplicates.
                    bound.setEClassifier(generate(genericArgument));
                    eTypeArgument.setEUpperBound(bound);
                }
            } else if (isTypeParameter(genericArgument, classifier)) {
                eTypeArgument.setETypeParameter(getETypeParameter(genericArgument.getFullTypeName(), classifier));
            } else {
                eTypeArgument.setEClassifier(generate(genericArgument));
            }
            addGenericArguments(eTypeArgument, genericArgument, classifier); // recursively add generic arguments
            genericType.getETypeArguments().add(eTypeArgument); // add ETypeArgument to original generic type
        }
    }

    /**
     * Adds all generic type parameters from an extracted type to a classifier.
     * @param classifier is the classifier.
     * @param type is the extracted type.
     */
    public void addTypeParameters(EClassifier classifier, ExtractedType type) {
        ETypeParameter eTypeParameter; // ecore type parameter
        EGenericType eBound; // ecore type parameter bound
        for (ExtractedTypeParameter typeParameter : type.getTypeParameters()) { // for all type parameters
            eTypeParameter = ecoreFactory.createETypeParameter(); // create object
            eTypeParameter.setName(typeParameter.getIdentifier()); // set name
            for (ExtractedDataType bound : typeParameter.getBounds()) { // for all bounds
                eBound = ecoreFactory.createEGenericType(); // create object
                if (isTypeParameter(bound, classifier)) {
                    eBound.setETypeParameter(getETypeParameter(bound.getFullTypeName(), classifier));
                } else {
                    eBound.setEClassifier(generate(bound));
                }
                addGenericArguments(eBound, bound, classifier); // add generic arguments of bound
                eTypeParameter.getEBounds().add(eBound); // add bound to type parameter
            }
            classifier.getETypeParameters().add(eTypeParameter); // add type parameter to EClassifier
        }
    }

    /**
     * Returns an EClassifier for an ExtractedDataType that can be used as DataType for Methods and Attributes.
     * @param extractedDataType is the extracted data type.
     * @return the data type as EClassifier, which is either (1.) a custom class from the model, or (2.) or an external
     * class that has to be created as data type, or (3.) an already known data type (Basic type or already created).
     */
    public EClassifier generate(ExtractedDataType extractedDataType) {
        EDataType eDataType;
        String fullName = extractedDataType.getFullTypeName();
        if (createdEClassifiers.containsKey(fullName)) { // if is custom class
            return createdEClassifiers.get(fullName);
        } else if (typeMap.containsKey(fullName)) { // if is basic type or already known
            eDataType = typeMap.get(fullName); // access EDataType
            return eDataType;
        } else { // if its an external type
            eDataType = create(extractedDataType); // create new EDataType
            root.getEClassifiers().add(eDataType); // add root containment
            return eDataType;
        }
    }

    /**
     * Returns an EGenericType for an ExtractedDataType that can be used as generic type for Methods and Attributes.
     * @param dataType is the ExtractedDataType.
     * @param classifier is the EClassifier that owns the the EGenericType
     * @return the EGenericType.
     */
    public EGenericType generate(ExtractedDataType dataType, EClassifier classifier) {
        if (isTypeParameter(dataType, classifier)) {
            EGenericType genericType = ecoreFactory.createEGenericType();
            genericType.setETypeParameter(getETypeParameter(dataType.getFullTypeName(), classifier));
            return genericType;
        }
        throw new IllegalArgumentException("The data type is not an type parameter: " + dataType.toString());
    }

    /**
     * Checks whether an extracted data type is a type parameter in a specific EClassifier.
     * @param dataType is the extracted data type.
     * @param classifier is the specific EClassifier.
     * @return true if the extracted data type is a type parameter.
     */
    public boolean isTypeParameter(ExtractedDataType dataType, EClassifier classifier) {
        String dataTypeName = dataType.getFullTypeName();
        for (ETypeParameter parameter : classifier.getETypeParameters()) {
            if (parameter.getName().equals(dataTypeName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Resets the class. Removes custom generated data types from the type map, so that only the basic types are
     * available.
     */
    public void reset() {
        typeMap.clear(); // clear map from all types.
        fillMap(); // add basic types.
    }

    /**
     * Setter for the root package, should be called before data types are getting created.
     * @param root is the root package.
     */
    public void setRoot(EPackage root) {
        this.root = root;
    }

    /**
     * Creates a new EDataType from an ExtractedDataType. The new EDataType can then be accessed from the type map.
     */
    private EDataType create(ExtractedDataType extractedDataType) {
        if (typeMap.containsKey(extractedDataType.getFullTypeName())) { // if already created:
            throw new IllegalArgumentException("Can't create an already created data type."); // throw exception
        }
        EDataType newType = ecoreFactory.createEDataType(); // new data type.
        newType.setName(extractedDataType.getTypeName()); // set name
        newType.setInstanceTypeName(extractedDataType.getFullTypeName()); // set full name
        typeMap.put(extractedDataType.getFullTypeName(), newType); // store in map for later use
        return newType;
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
        typeMap.put("java.util.List", EcorePackage.eINSTANCE.getEEList());
        typeMap.put("java.util.Map", EcorePackage.eINSTANCE.getEMap());
    }

    /**
     * Gets an ETypeParameter with a specific name from an specific EClassifier.
     */
    private ETypeParameter getETypeParameter(String name, EClassifier classifier) {
        for (ETypeParameter parameter : classifier.getETypeParameters()) {
            if (parameter.getName().equals(name)) {
                return parameter;
            }
        }
        throw new IllegalArgumentException("There is no ETypeParameter " + name + " in " + classifier.toString());
    }
}