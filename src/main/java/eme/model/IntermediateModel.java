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

    public IntermediateModel() {
        packages = new LinkedList<ExtractedPackage>();
        // TODO BUILD CLASS
    }

}
