package eme.handlers;

import org.eclipse.core.resources.IProject;

import eme.EcoreMetamodelExtraction;

/**
 * Handler for calling an extraction method.
 * @author Timur Saglam
 */
public class ExtractHandler extends MainHandler {

    /**
     * @see eme.handlers.MainHandler#startExtraction(org.eclipse.core.resources.IProject)
     */
    @Override
    protected void startExtraction(IProject project) {
        new EcoreMetamodelExtraction().extractFrom(project);
    }
}