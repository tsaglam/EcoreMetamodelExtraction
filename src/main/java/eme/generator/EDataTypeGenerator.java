package eme.generator;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;

import eme.model.datatypes.ExtractedDataType;

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
        this.createdEClassifiers = createdEClassifiers;
        ecoreFactory = EcoreFactory.eINSTANCE;
        typeMap = new HashMap<String, EDataType>();
        fillMap();
    }

    /**
     * Adds all generic arguments from an extracted data type to an generic type. For all generic arguments add their
     * generic arguments recursively.
     * @param genericType is the generic type of an attribute, a parameter or a method.
     * @param dataType is the extracted data type, an attribute, a parameter or a return type.
     */
    public void addGenericArguments(EGenericType genericType, ExtractedDataType dataType) {
        for (ExtractedDataType genericArgument : dataType.getGenericArguments()) { // for every generic argument
            EGenericType eTypeArgument = ecoreFactory.createEGenericType(); // create ETypeArgument as EGenericType
            eTypeArgument.setEClassifier(generateFrom(genericArgument)); // set data type
            addGenericArguments(eTypeArgument, genericArgument); // recursively add generic arguments of this one
            genericType.getETypeArguments().add(eTypeArgument); // add ETypeArgument to original generic type
        }
    }

    /**
     * Returns an EClassifier for an ExtractedDataType that can be used as DataType for Methods and Attributes.
     * @param extractedDataType is the extracted data type.
     * @return the data type as EClassifier, which is either (1.) a custom class from the model, or (2.) or an external
     * class that has to be created as data type, or (3.) an already known data type (Basic type or already created).
     */
    public EClassifier generateFrom(ExtractedDataType extractedDataType) {
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
     * Resets the class. Removes custom generated data types from the type map, so that only the basic types are
     * available.
     */
    public void reset() {
        typeMap.clear();
        fillMap();
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
        if (typeMap.containsKey(extractedDataType.getFullTypeName())) {
            throw new IllegalArgumentException("Can't create an already created data type.");
        }
        EDataType newType = ecoreFactory.createEDataType();
        newType.setName(extractedDataType.getTypeName());
        newType.setInstanceTypeName(extractedDataType.getFullTypeName());
        typeMap.put(extractedDataType.getFullTypeName(), newType);
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
}