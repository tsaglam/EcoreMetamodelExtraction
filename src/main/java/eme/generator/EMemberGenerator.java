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
import org.eclipse.emf.ecore.ETypedElement;
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
     * Adds any {@link ExtractedField} of an {@link ExtractedType} to a specific {@link EClass}.
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
     * Adds any {@link ExtractedMethod} of an {@link ExtractedType} to an {@link EClass}.
     * @param type is the {@link ExtractedType}.
     * @param eClass is the {@link EClass}.
     */
    public void addOperations(ExtractedType type, EClass eClass) {
        for (ExtractedMethod method : type.getMethods()) { // for every method
            if (selector.allowsGenerating(method)) { // if should be generated.
                addOperation(method, eClass);
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
     * with an undefined upper bound property, which represents an one-to-many reference. If it is a reference,
     * containment has to be set manually.
     */
    private void addField(ExtractedField field, EClass eClass) {
        ExtractedDataType dataType = getRelevantDataType(field);
        EStructuralFeature representation = createFieldRepresentation(dataType);
        representation.setName(field.getIdentifier()); // set name
        representation.setChangeable(!(field.isFinal() && selector.allowsUnchangeable())); // make unchangeable if final
        setUpperBound(representation, field);
        typeGenerator.addDataType(representation, dataType, new TypeParameterSource(eClass)); // add type to attribute
        eClass.getEStructuralFeatures().add(representation); // add feature to EClass
    }

    /**
     * Adds a single {@link ExtractedMethod} to a {@link EClass} by creating a {@link EOperation} as Ecore
     * representation.
     */
    private void addOperation(ExtractedMethod method, EClass eClass) {
        EOperation operation = ecoreFactory.createEOperation(); // create object
        operation.setName(method.getName()); // set name
        eClass.getEOperations().add(operation);
        typeGenerator.addTypeParameters(operation, method);
        TypeParameterSource source = new TypeParameterSource(operation); // source of type parameters
        addReturnType(operation, method.getReturnType(), source); // add return type
        addExceptions(operation, method, source); // add throws declarations
        addParameters(method, operation.getEParameters(), source); // add parameters
    }

    /**
     * Adds the parameters of an {@link ExtractedMethod} to a specific List of {@link EParameter}s.
     */
    private void addParameters(ExtractedMethod method, List<EParameter> list, TypeParameterSource source) {
        EParameter eParameter;
        for (ExtractedParameter parameter : method.getParameters()) { // for every parameter
            eParameter = ecoreFactory.createEParameter();
            eParameter.setName(parameter.getIdentifier()); // set identifier
            setUpperBound(eParameter, parameter);
            typeGenerator.addDataType(eParameter, getRelevantDataType(parameter), source); // add type to EParameter
            list.add(eParameter);
        }
    }

    /**
     * Adds the return type of an {@link ExtractedMethod} to an {@link EOperation}.
     */
    private void addReturnType(EOperation operation, ExtractedDataType returnType, TypeParameterSource source) {
        if (returnType != null) { // if return type is not void
            setUpperBound(operation, returnType);
            typeGenerator.addDataType(operation, getRelevantDataType(returnType), source); // add type to return type
        }
    }

    /**
     * Factory method for the Ecore representations of any {@link ExtractedDataType}.
     */
    private EStructuralFeature createFieldRepresentation(ExtractedDataType dataType) {
        if (isEClass(dataType)) { // if type is EClass:
            return ecoreFactory.createEReference();
        } else { // if it is EDataType:
            return ecoreFactory.createEAttribute();
        }
    }

    /**
     * This method returns the list data type of any {@link ExtractedDataType} which is of type {@link List} when
     * one-to-many multiplicities are allowed or the {@link ExtractedDataType} itself for any other case. This ensures
     * that always the right data type is used for the Ecore representation of an {@link ExtractedDataType}. For
     * example, a data type List<String> will return the data type String, but Map<String, String> will return
     * Map<String, String>.
     */
    private ExtractedDataType getRelevantDataType(ExtractedDataType dataType) {
        if (isMultiplicityRepresentable(dataType)) {
            return dataType.getGenericArguments().get(0); // get type of generic argument: List<String> => String
        }
        return dataType; // base case: return data type itself
    }

    /**
     * Checks whether a specific {@link ExtractedDataType} is an already created EClass.
     */
    private boolean isEClass(ExtractedDataType dataType) {
        String typeName = dataType.getFullType();
        return eClassifierMap.containsKey(typeName) && !(eClassifierMap.get(typeName) instanceof EEnum);
    }

    /**
     * Checks whether a {@link ExtractedDataType} can be represented in the Ecore metamodel by using multiplicities.
     * This depends on three conditions: The data type is a list type (@see {@link ExtractedDataType#isListType()}), the
     * list is not a list of wild card types, and the user allowed the use of multiplicities in the settings.
     */
    private boolean isMultiplicityRepresentable(ExtractedDataType dataType) {
        return dataType.isListType() && !dataType.getGenericArguments().get(0).isWildcard() && selector.allowsMultiplicities(dataType);
    }

    /**
     * Sets the upper bound of a {@link ETypedElement} depending on whether the {@link ExtractedDataType} represents is
     * a list type and whether one-to-many multiplicities are allowed.
     */
    private void setUpperBound(ETypedElement typedElement, ExtractedDataType dataType) {
        if (isMultiplicityRepresentable(dataType)) {
            typedElement.setUpperBound(-1); // set to one-to-many multiplicity
        }
    }
}