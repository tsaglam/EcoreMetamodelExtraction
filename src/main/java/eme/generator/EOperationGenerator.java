package eme.generator;

import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.EcoreFactory;

import eme.model.ExtractedMethod;
import eme.model.ExtractedType;
import eme.model.datatypes.ExtractedDataType;
import eme.model.datatypes.ExtractedParameter;

/**
 * Generator class for Ecore operations ({@link EOperation}s).
 * @author Timur Saglam
 */
public class EOperationGenerator {
    private final EcoreFactory ecoreFactory;
    private final SelectionHelper selector;
    private final EDataTypeGenerator typeGenerator;

    /**
     * Basic constructor.
     * @param typeGenerator is the {@link EDataTypeGenerator} instance.
     * @param selector is the {@link SelectionHelper} instance.
     */
    public EOperationGenerator(EDataTypeGenerator typeGenerator, SelectionHelper selector) {
        this.typeGenerator = typeGenerator;
        this.selector = selector;
        ecoreFactory = EcoreFactory.eINSTANCE;
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
                addReturnType(operation, method.getReturnType(), eClass); // add return type
                addExceptions(operation, method, eClass); // add throws declarations
                addParameters(method, operation.getEParameters(), eClass); // add parameters
                eClass.getEOperations().add(operation);
            }
        }
    }

    /**
     * Adds the declared exceptions of an {@link ExtractedMethod} to an {@link EOperation}.
     */
    private void addExceptions(EOperation operation, ExtractedMethod method, EClass eClass) {
        for (ExtractedDataType exception : method.getThrowsDeclarations()) {
            typeGenerator.addException(operation, exception, eClass);
        }
    }

    /**
     * Adds the parameters of an {@link ExtractedMethod} to a specific List of {@link EParameter}s.
     */
    private void addParameters(ExtractedMethod method, List<EParameter> list, EClass eClass) {
        EParameter eParameter;
        for (ExtractedParameter parameter : method.getParameters()) { // for every parameter
            eParameter = ecoreFactory.createEParameter();
            eParameter.setName(parameter.getIdentifier()); // set identifier
            typeGenerator.addDataType(eParameter, parameter, eClass); // add type type to EParameter
            list.add(eParameter);
        }
    }

    /**
     * Adds the return type of an {@link ExtractedMethod} to an {@link EOperation}.
     */
    private void addReturnType(EOperation operation, ExtractedDataType returnType, EClass eClass) {
        if (returnType != null) { // if return type is not void
            typeGenerator.addDataType(operation, returnType, eClass); // add type to return type
        }
    }
}