package eme.generator;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.ETypeParameter;
import org.eclipse.emf.ecore.impl.EOperationImpl;

import eme.model.datatypes.ExtractedDataType;

/**
 * This class serves as source for {@link ETypeParameter}s. It contains an {@link EClassifier} and sometimes an
 * {@link EOperation}. Both the class {@link EClassifier} and {@link EOperation} can own {@link ETypeParameter}s. Both
 * of them have a method to access them. But despite the identical signature of the method, this method is not inherited
 * by a common super class or defined in a common interface. This class bypasses this problem by defining one class that
 * can return {@link ETypeParameter}s from both an {@link EOperation} and an {@link EClassifier}. Additionally, when
 * created from an {@link EOperation}, it can be used to locate {@link ETypeParameter} from the {@link EOperation} and
 * the containing {@link EClass} of the {@link EOperation} at the same time.
 * @author Timur Saglam
 */
public class TypeParameterSource {
    private final EClassifier classifier;
    private EOperation operation;

    /**
     * Creates new type parameter source from an {@link EClassifier}.
     * @param classifier is the source {@link EClassifier}.
     */
    public TypeParameterSource(EClassifier classifier) {
        this(classifier, null); // no operation
        operation = new NullOperation();
    }

    /**
     * Creates new type parameter source from an {@link EOperation}.
     * @param operation is the source {@link EOperation}. It has to be contained in an {@link EClassifier}.
     */
    public TypeParameterSource(EOperation operation) {
        this(operation.getEContainingClass(), operation); // implicit EClassifier
    }

    /**
     * Creates new type parameter source.
     * @param classifier is an {@link EClassifier} as source.
     * @param operation is an {@link EOperation} as source.
     */
    private TypeParameterSource(EClassifier classifier, EOperation operation) {
        this.classifier = classifier;
        this.operation = operation;
    }

    /**
     * Checks whether an {@link ETypeParameter} in this {@link ETypeParameter} source matches an
     * {@link ExtractedDataType}.
     * @param dataType is the {@link ExtractedDataType} whose name is used to find a matching {@link ETypeParameter}.
     * @return true if it found a matching {@link ETypeParameter}.
     */
    public boolean containsTypeParameter(ExtractedDataType dataType) {
        return getTypeParameter(dataType) != null;
    }

    /**
     * Finds an {@link ETypeParameter} in this {@link ETypeParameter} source which matches an {@link ExtractedDataType}.
     * @param dataType is the {@link ExtractedDataType} whose name is used to search the {@link ETypeParameter}.
     * @return the {@link ETypeParameter} or null if there is no matching {@link ETypeParameter}.
     */
    public ETypeParameter getTypeParameter(ExtractedDataType dataType) {
        String name = dataType.getFullType();
        ETypeParameter parameter = find(name, operation.getETypeParameters());
        if (parameter == null) {
            return find(name, classifier.getETypeParameters());
        }
        return parameter;
    }

    @Override
    public String toString() {
        List<ETypeParameter> classParameters = new LinkedList<>(classifier.getETypeParameters());
        List<ETypeParameter> operationParameters = new LinkedList<>(operation.getETypeParameters());
        return getClass().getSimpleName() + "(fromClass" + classParameters + " fromMethod" + operationParameters + ")";
    }

    /**
     * Finds and returns an {@link ETypeParameter} in an {@link List} of {@link ETypeParameter}s.
     */
    private ETypeParameter find(String name, EList<ETypeParameter> list) {
        for (ETypeParameter parameter : list) {
            if (parameter.getName().equals(name)) {
                return parameter;
            }
        }
        return null;
    }

    private class NullOperation extends EOperationImpl {
        @Override
        public EList<ETypeParameter> getETypeParameters() {
            return new BasicEList<ETypeParameter>();
        }
    }
}