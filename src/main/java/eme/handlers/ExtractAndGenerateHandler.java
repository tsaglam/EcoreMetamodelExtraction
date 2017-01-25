package eme.handlers;

import org.eclipse.core.resources.IProject;

import eme.EcoreMetamodelExtraction;

/**
 * Handler for calling an extraction method.
 * @author Timur Saglam
 */
public class ExtractAndGenerateHandler extends MainHandler {

    /**
     * Basic constructor, calls {@link eme.handlers.MainHandler#MainHandler()}
     */
    public ExtractAndGenerateHandler() {
        super();
    }

    /**
     * @see eme.handlers.MainHandler#startExtraction(org.eclipse.core.resources.IProject)
     */
    @Override
    protected void startExtraction(IProject project) {
        new EcoreMetamodelExtraction().extractAndGenerateFrom(project);
    }
}