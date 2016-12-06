package eme.model;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcoreFactory;

/**
 * Superclass of all extracted elements of the intermediate model. The intermediate model is the
 * temporary model between the implicit model of the code files and the Ecore metamodel.
 * @author Timur Saglam
 */
public abstract class ExtractedElement {
    protected EcoreFactory ecoreFactory;

    /**
     * Gets the instance of the Ecore Factory.
     */
    public ExtractedElement() {
        ecoreFactory = EcoreFactory.eINSTANCE;
    }

    /**
     * Generates and returns the Ecore representation of the extracted element.
     * @return the Ecore representation of the extracted element.
     */
    public abstract EObject generateEcoreRepresentation();
}
