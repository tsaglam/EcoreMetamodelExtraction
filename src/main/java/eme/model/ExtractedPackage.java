package eme.model;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

/**
 * Is the representation of a package in the intermediate model.
 * @author Timur Saglam
 */
public class ExtractedPackage extends ExtractedElement {
    private String name;
    private String prefix;
    private String uri;
    
    /**
     * Creates an extracted package.
     * @param name is the name of the EPackage.
     * @param prefix is the preferred XMLNS prefix for the namespace URI of the EPackage.
     * @param uri is the namespace URI, a universally unique identification of the EPackage..
     */
    public ExtractedPackage(String name, String prefix, String uri) {
        this.name = name;
        this.prefix = prefix;
        this.uri = uri;
    }

    /**
     * @see eme.model.ExtractedElement#generateEcoreRepresentation()
     */
    @Override
    public EObject generateEcoreRepresentation() {
        EPackage ePackage = ecoreFactory.createEPackage();
        ePackage.setName(name);
        ePackage.setNsPrefix(prefix);
        ePackage.setNsURI(uri);
        return ePackage;
    }

}
