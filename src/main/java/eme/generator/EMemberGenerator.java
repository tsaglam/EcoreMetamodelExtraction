package eme.generator;

import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;

import eme.model.ExtractedMethod;
import eme.model.ExtractedType;
import eme.model.datatypes.ExtractedDataType;
import eme.model.datatypes.ExtractedField;
import eme.model.datatypes.ExtractedParameter;

/**
 * Generator class for Ecore members ({@link EOperation}s and {@link EStructuralFeature}s).
 * @author Timur Saglam
 */
public class EMemberGenerator {
    private final Map<String, EClassifier> eClassifierMap;
    private final EcoreFactory ecoreFactory;
    private final SelectionHelper selector;
    private final EDataTypeGenerator typeGenerator;

    /**
     * Basic constructor.
     * @param typeGenerator is the {@link EDataTypeGenerator} instance.
     * @param selector is the {@link SelectionHelper} instance.
     * @param eClassifierMap is the map of already generated {@link EClassifier}s.
     */
    public EMemberGenerator(EDataTypeGenerator typeGenerator, SelectionHelper selector, Map<String, EClassifier> eClassifierMap) {
        this.typeGenerator = typeGenerator;
        this.selector = selector;
        this.eClassifierMap = eClassifierMap;
        ecoreFactory = EcoreFactory.eINSTANCE;
    }

    /**
     * Adds all the fields of an {@link ExtractedType} to a specific {@link EClass}.
     * @param type is the {@link ExtractedType}
     * @param eClass is the {@link EClass}.
     */
    public void addFields(ExtractedType type, EClass eClass) {
        for (ExtractedField field : type.getFields()) { // for every field
            if (selector.allowsGenerating(field)) { // if it is selected
                addField(field, eClass); // add to EClass by creating an Ecore representation
            }
        }
    }

    /**
     * Adds the operations of an {@link ExtractedType} to an {@link EClass}.
     * @param type is the {@link ExtractedType}.
     * @param eClass is the {@link EClass}.
     */
    public void addOperations(ExtractedType type, EClass eClass) {
        EOperation operation;
        for (ExtractedMethod method : type.getMethods()) { // for every method
            if (selector.allowsGenerating(method)) { // if should be generated.
                operation = ecoreFactory.createEOperation(); // create object
                operation.setName(method.getName()); // set name
                eClass.getEOperations().add(operation);
                typeGenerator.addTypeParameters(operation, method);
                TypeParameterSource source = new TypeParameterSource(operation); // source of type parameters
                addReturnType(operation, method.getReturnType(), source); // add return type
                addExceptions(operation, method, source); // add throws declarations
                addParameters(method, operation.getEParameters(), source); // add parameters
            }
        }
    }

    /**
     * Adds a root container {@link EReference} to an root container {@link EClass}. The root container
     * {@link EReference} is a one-to-many reference to {@link EObject}.
     * @param rootContainer is the root container {@link EClass}.
     */
    public void addRootContainerReference(EClass rootContainer) {
        EReference reference = ecoreFactory.createEReference();
        reference.setName("containedElements");
        reference.setUpperBound(-1); // one to many relation
        reference.setEType(EcorePackage.eINSTANCE.getEObject());
        rootContainer.getEStructuralFeatures().add(reference);
    }

    /**
     * Adds the declared exceptions of an {@link ExtractedMethod} to an {@link EOperation}.
     */
    private void addExceptions(EOperation operation, ExtractedMethod method, TypeParameterSource source) {
        for (ExtractedDataType exception : method.getThrowsDeclarations()) {
            typeGenerator.addException(operation, exception, source);
        }
    }

    /**
     * Adds a field to a {@link EClass} by creating a {@link EStructuralFeature} as Ecore representation, which is
     * either a {@link EReference} or an {@link EAttribute}. List types are represented by an {@link EStructuralFeature}
     * with an undefined upper bound property, which represents an one-to-many reference.
     */
    private void addField(ExtractedField field, EClass eClass) {
        ExtractedDataType dataType = field;
        if (field.isListType() && selector.allowsMultiplicities()) { // only if one-to-many multiplicities are enabled
            dataType = field.getGenericArguments().get(0); // get type of generic argument: List<String> => String
        }
        EStructuralFeature representation = getRepresentation(dataType);
        addStructuralFeature(representation, dataType, field, eClass); // build reference
    }

    /**
     * Adds the parameters of an {@link ExtractedMethod} to a specific List of {@link EParameter}s.
     */
    private void addParameters(ExtractedMethod method, List<EParameter> list, TypeParameterSource source) {
        EParameter eParameter;
        for (ExtractedParameter parameter : method.getParameters()) { // for every parameter
            eParameter = ecoreFactory.createEParameter();
            eParameter.setName(parameter.getIdentifier()); // set identifier
            typeGenerator.addDataType(eParameter, parameter, source); // add type type to EParameter
            list.add(eParameter);
        }
    }

    /**
     * Adds the return type of an {@link ExtractedMethod} to an {@link EOperation}.
     */
    private void addReturnType(EOperation operation, ExtractedDataType returnType, TypeParameterSource source) {
        if (returnType != null) { // if return type is not void
            typeGenerator.addDataType(operation, returnType, source); // add type to return type
        }
    }

    /**
     * Builds a structural feature from an extracted attribute and adds it to an EClass. A structural feature can be an
     * EAttribute or an EReference. If it is a reference, containment has to be set manually.
     */
    private void addStructuralFeature(EStructuralFeature feature, ExtractedDataType dataType, ExtractedField field, EClass eClass) {
        feature.setName(field.getIdentifier()); // set name
        feature.setChangeable(!(field.isFinal() && selector.allowsUnchangeable())); // make unchangeable if final
        if (!dataType.equals(field)) { // if is list type
            feature.setUpperBound(-1); // no upper bound
        }
        typeGenerator.addDataType(feature, dataType, new TypeParameterSource(eClass)); // add type to attribute
        eClass.getEStructuralFeatures().add(feature); // add feature to EClass
    }

    /**
     * Factory method for the Ecore representations of any {@link ExtractedDataType}.
     */
    private EStructuralFeature getRepresentation(ExtractedDataType dataType) {
        if (isEClass(dataType)) { // if type is EClass:
            return ecoreFactory.createEReference();
        } else { // if it is EDataType:
            return ecoreFactory.createEAttribute();
        }
    }

    /**
     * Checks whether a specific type name is an already created EClass.
     */
    private boolean isEClass(ExtractedDataType dataType) {
        String typeName = dataType.getFullType();
        return eClassifierMap.containsKey(typeName) && !(eClassifierMap.get(typeName) instanceof EEnum);
    }
}