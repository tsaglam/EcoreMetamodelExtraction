package eme.generator;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;

import eme.model.datatypes.ExtractedDataType;

/**
 * Generator class for the generation of Ecore data types: EDataTypes
 * @author Timur Saglam
 */
public class EDataTypeGenerator {
    private final EcoreFactory ecoreFactory;
    private final Map<String, EDataType> typeMap;

    /**
     * Basic constructor, builds the type map.
     */
    public EDataTypeGenerator() {
        ecoreFactory = EcoreFactory.eINSTANCE;
        typeMap = new HashMap<String, EDataType>();
        fillMap();
    }

    /**
     * Creates a new EDataType from an ExtractedDataType. The new EDataType can then be accessed with the method
     * EDataTypeGenerator.get().
     * @param extractedDataType is the extracted data type.
     * @return the new EDataType
     */
    public EDataType create(ExtractedDataType extractedDataType) {
        if (knows(extractedDataType)) {
            throw new IllegalArgumentException("Can't create an already created data type.");
        }
        EDataType newType = ecoreFactory.createEDataType();
        newType.setName(extractedDataType.getTypeName());
        newType.setInstanceTypeName(extractedDataType.getFullTypeName());
        typeMap.put(extractedDataType.getFullTypeName(), newType);
        return newType;
    }

    /**
     * Returns the EDataType for a specific extracted data type, if it exists.
     * @param extractedDataType is the specific data type.
     * @return the EDataType, or null if the data type name is unknown.
     */
    public EDataType get(ExtractedDataType extractedDataType) {
        if (!knows(extractedDataType)) {
            throw new IllegalArgumentException("Unknown data type. Create the data type first.");
        }
        return typeMap.get(extractedDataType.getFullTypeName());
    }

    /**
     * Checks whether a specific extracted data type can be accessed using the method EDataTypeGenerator.get(). If it
     * can not be accessed it has to be created first.
     * @param extractedDataType is the data type.
     * @return true if it is a basic type.
     */
    public boolean knows(ExtractedDataType extractedDataType) {
        return typeMap.containsKey(extractedDataType.getFullTypeName());
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