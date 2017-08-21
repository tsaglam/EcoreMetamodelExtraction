package eme.generator;

import java.util.List;
import java.util.Map;

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
public class MemberGenerator {
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
    public MemberGenerator(EDataTypeGenerator typeGenerator, SelectionHelper selector, Map<String, EClassifier> eClassifierMap) {
        this.typeGenerator = typeGenerator;
        this.selector = selector;
        this.eClassifierMap = eClassifierMap;
        ecoreFactory = EcoreFactory.eINSTANCE;
    }

    /**
     * Adds the fields of an {@link ExtractedType} to a specific {@link EClass}.
     * @param type is the {@link ExtractedType}
     * @param eClass is the {@link EClass}.
     */
    public void addFields(ExtractedType type, EClass eClass) {
        for (ExtractedField field : type.getFields()) { // for every attribute
            if (selector.allowsGenerating(field)) { // if should be generated:
                if (isEClass(field.getFullType())) { // if type is EClass:
                    EReference reference = ecoreFactory.createEReference();
                    reference.setContainment(true); // has to be contained
                    addStructuralFeature(reference, field, eClass); // build reference
                } else { // if it is EDataType:
                    addStructuralFeature(ecoreFactory.createEAttribute(), field, eClass); // build attribute
                }
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
        // reference.setContainment(true); // TODO (HIGH) is this needed?
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
    private void addStructuralFeature(EStructuralFeature feature, ExtractedField field, EClass eClass) {
        feature.setName(field.getIdentifier()); // set name
        feature.setChangeable(!field.isFinal()); // make unchangeable if final
        typeGenerator.addDataType(feature, field, new TypeParameterSource(eClass)); // add type to attribute
        eClass.getEStructuralFeatures().add(feature); // add feature to EClass
    }

    /**
     * Checks whether a specific type name is an already created EClass.
     */
    private boolean isEClass(String typeName) {
        return eClassifierMap.containsKey(typeName) && !(eClassifierMap.get(typeName) instanceof EEnum);
    }
}