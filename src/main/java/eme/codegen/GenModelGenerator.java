package eme.codegen;

import java.io.IOException;
import java.util.Collections;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.codegen.ecore.genmodel.GenJDKLevel;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.codegen.ecore.genmodel.GenModelFactory;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;

import eme.generator.saving.SavingInformation;

/**
 * Creates generation models from Ecore metamodels.
 * @author Timur Saglam
 */
public class GenModelGenerator {
    private static final Logger logger = LogManager.getLogger(GenModelGenerator.class.getName());
    private final GenJDKLevel complianceLevel;
    private final String importerID;
    private final String rootExtendsClass;
    private final String xmlEncoding;

    /**
     * Basic constructor builds GenModelGenerator with default values.
     */
    public GenModelGenerator() {
        complianceLevel = GenJDKLevel.JDK80_LITERAL;
        importerID = "org.eclipse.emf.importer.ecore";
        rootExtendsClass = "org.eclipse.emf.ecore.impl.MinimalEObjectImpl$Container";
        xmlEncoding = "UTF-8";
    }

    /**
     * Constructor that builds the GenModelGenerator with custom values.
     * @param complianceLevel is the compliance level (see {@link GenJDKLevel})
     * @param importerID is the the new value of the 'Importer ID' attribute.
     * @param rootExtendsClass is the value of the 'Root Extends Class' attribute.
     * @param xmlEncoding is the XML encoding (e.g. UTF-8 or ASCII)
     */
    public GenModelGenerator(GenJDKLevel complianceLevel, String importerID, String rootExtendsClass, String xmlEncoding) {
        this.complianceLevel = complianceLevel;
        this.importerID = importerID;
        this.rootExtendsClass = rootExtendsClass;
        this.xmlEncoding = xmlEncoding;
    }

    /**
     * Generates a generator model for a Ecore metamodel and saves it in the same folder as the metamodel.
     * @param ecoreMetamodel is the Ecore metamodel, passed through its root package.
     * @param information is the saving information object. It contains information about the location of the metamodel.
     * @return the generator model, a GenModel object.
     */
    public GenModel generate(EPackage ecoreMetamodel, SavingInformation information) {
        String modelName = information.getFileName();
        String modelPath = information.getFilePath();
        String projectPath = modelPath.substring(0, modelPath.lastIndexOf('/', modelPath.lastIndexOf('/') - 1));
        String projectName = projectPath.substring(projectPath.lastIndexOf('/'));
        GenModel genModel = GenModelFactory.eINSTANCE.createGenModel();
        genModel.setModelDirectory(projectName + "/src");
        genModel.setModelPluginID(projectName.substring(1));
        genModel.setModelName(modelName);
        genModel.setRootExtendsClass(rootExtendsClass);
        genModel.setImporterID(importerID);
        genModel.setComplianceLevel(complianceLevel);
        genModel.setOperationReflection(true);
        genModel.setImportOrganizing(true);
        genModel.getForeignModel().add(modelName + ".ecore");
        genModel.initialize(Collections.singleton(ecoreMetamodel));
        saveGenModel(genModel, modelPath, modelName);
        return genModel;
    }

    /**
     * Saves a GenModel as a file and refreshes the output folder.
     */
    private void saveGenModel(GenModel genModel, String modelPath, String modelName) {
        try {
            URI genModelURI = URI.createFileURI(modelPath + modelName + ".genmodel");
            final XMIResourceImpl genModelResource = new XMIResourceImpl(genModelURI);
            genModelResource.getDefaultSaveOptions().put(XMLResource.OPTION_ENCODING, xmlEncoding);
            genModelResource.getContents().add(genModel);
            genModelResource.save(Collections.EMPTY_MAP);
            IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot(); // refresh workspace folder:
            root.getContainerForLocation(new Path(modelPath)).refreshLocal(IResource.DEPTH_INFINITE, null);
        } catch (IOException exception) {
            logger.error("Error while saving the generator model: ", exception);
        } catch (CoreException exception) {
            logger.warn("Could not refresh output folder. Try that manually.", exception);
        }
        logger.info("The genmodel was saved under: " + modelPath);
    }
}