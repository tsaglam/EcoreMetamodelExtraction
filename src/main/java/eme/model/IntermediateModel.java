package eme.model;

import java.util.LinkedList;
import java.util.List;

/**
 * Base class for an intermediate model.
 * @author Timur Saglam
 */
public class IntermediateModel {
    private ExtractedElement rootElement;
    private List<ExtractedPackage> packages;

    /**
     * TODO comment constructor.
     */
    public IntermediateModel() {
        packages = new LinkedList<ExtractedPackage>();
        // TODO BUILD CLASS
    }
    
    /**
     * TODO comment constructor.
     * @param rootElement
     */
    public IntermediateModel(ExtractedElement rootElement) {
        super();
        this.rootElement = rootElement;
    }
    
}
