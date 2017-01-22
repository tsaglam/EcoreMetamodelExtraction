package eme.generator;

import java.io.IOException;
import java.util.Collections;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.core.resources.IContainer;
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
public abstract class EcoreGenmodelGenerator {
    private static final GenJDKLevel COMPLIANCE_LEVEL = GenJDKLevel.JDK80_LITERAL;
    private static final String IMPORTER_ID = "org.eclipse.emf.importer.ecore";
    private static final Logger logger = LogManager.getLogger(EcoreGenmodelGenerator.class.getName());
    private static final String ROOT_EXTENDS_CLASS = "org.eclipse.emf.ecore.impl.MinimalEObjectImpl$Container";
    private static final String XML_ENCODING = "UTF-8";

    /**
     * Generates a generator model for a Ecore metamodel and saves it in the same folder as the metamodel.
     * @param ecoreMetamodel is the Ecore metamodel, passed through its root package.
     * @param information is the saving information object. It contains information about the location of the metamodel.
     * @return the generator model, a GenModel object.
     */
    public static GenModel generate(EPackage ecoreMetamodel, SavingInformation information) {
        String path = information.getFilePath();
        String projectPath = path.substring(0, path.lastIndexOf('/', path.lastIndexOf('/') - 1));
        String projectName = projectPath.substring(projectPath.lastIndexOf('/'));
        String name = information.getFileName();
        GenModel genModel = GenModelFactory.eINSTANCE.createGenModel();
        genModel.setModelDirectory(projectName + "/src");
        genModel.setModelPluginID(projectName.substring(1));
        genModel.setModelName(name);
        genModel.setRootExtendsClass(ROOT_EXTENDS_CLASS);
        genModel.setImporterID(IMPORTER_ID);
        genModel.setComplianceLevel(COMPLIANCE_LEVEL);
        genModel.setOperationReflection(true);
        genModel.setImportOrganizing(true);
        genModel.getForeignModel().add(name + ".ecore");
        genModel.initialize(Collections.singleton(ecoreMetamodel));
        try {
            URI genModelURI = URI.createFileURI(path + name + ".genmodel");
            final XMIResourceImpl genModelResource = new XMIResourceImpl(genModelURI);
            genModelResource.getDefaultSaveOptions().put(XMLResource.OPTION_ENCODING, XML_ENCODING);
            genModelResource.getContents().add(genModel);
            genModelResource.save(Collections.EMPTY_MAP);
        } catch (IOException exception) {
            logger.error(exception);
        }
        refreshFolder(path);
        logger.info("The genmodel was saved under: " + path);
        return genModel;
    }

    /**
     * Refreshes a specific folder in the Eclipse IDE
     * @param folderPath is the path of the folder.
     */ // TODO (MEDIUM) duplicate in saving strategy
    private static void refreshFolder(String folderPath) {
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        IContainer folder = root.getContainerForLocation(new Path(folderPath));
        try {
            folder.refreshLocal(IResource.DEPTH_INFINITE, null);
        } catch (CoreException exception) {
            logger.warn("Could not refresh output folder. Try that manually.", exception);
        }
    }
}