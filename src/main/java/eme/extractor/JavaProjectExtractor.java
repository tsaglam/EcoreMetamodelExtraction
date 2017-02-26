package eme.extractor;

import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import eme.model.ExtractedPackage;
import eme.model.IntermediateModel;

/**
 * The class analyzes java projects and builds intermediate models with the help of the {@link JavaTypeExtractor},
 * {@link JavaMethodExtractor} and {@link DataTypeExtractor}.
 * @author Timur Saglam
 */
public class JavaProjectExtractor {
    private static final Logger logger = LogManager.getLogger(JavaProjectExtractor.class.getName());
    private IntermediateModel currentModel;
    private ExtractedPackage currentPackage;
    private DataTypeExtractor dataTypeParser;
    private int packageCounter;
    private JavaTypeExtractor typeParser;

    /**
     * Analyzes a {@link IJavaProject} and builds an {@link IntermediateModel}.
     * @param project is the {@link IJavaProject} to analyze.
     * @return an intermediate model that was extracted from the project.
     */
    public IntermediateModel buildIntermediateModel(IJavaProject project) {
        setup(project);
        try {
            parsePackages(project); // parse project
            typeParser.parseExternalTypes(dataTypeParser.getDataTypes()); // parse potential external
        } catch (JavaModelException exception) {
            throw new RuntimeException("Error while extracting the model.", exception);
        }
        currentModel.sort(); // sort model content
        currentModel.print(); // print intermediate model.
        return currentModel;
    }

    /**
     * Checks if a {@link IPackageFragment} is a source package.
     */
    private boolean isSourcePackage(IPackageFragment packageFragment) throws JavaModelException {
        return packageFragment.getKind() == IPackageFragmentRoot.K_SOURCE;
    }

    /**
     * Extracts all compilation units from a list of package fragments. It then parses all ICompilationUnits while
     * updating the current package.
     */
    private void parseCompilationUnits(List<IPackageFragment> fragments) throws JavaModelException {
        for (IPackageFragment fragment : fragments) { // for every package fragment
            currentPackage = currentModel.getPackage(fragment.getElementName()); // model package
            reportProgress(fragments.size());
            for (ICompilationUnit unit : fragment.getCompilationUnits()) { // get compilation units
                for (IType type : unit.getAllTypes()) { // for all types
                    currentModel.addTo(typeParser.parseType(type), currentPackage);
                }
            }
        }
    }

    /**
     * The method takes an {@link IJavaProject} and extracts the package structure of the project. It continues by
     * parsing the {@link IPackageFragment}s. The method creates the packages from a set of package names to avoid the
     * problem of duplicate default packages. But all other parsing calls are done with a list of fragments.
     */
    private void parsePackages(IJavaProject project) throws JavaModelException {
        SortedSet<String> packageNames = new TreeSet<String>(); // set to avoid duplicates
        List<IPackageFragment> fragments = new LinkedList<IPackageFragment>();
        for (IPackageFragment fragment : project.getPackageFragments()) {
            if (isSourcePackage(fragment)) { // only source packages, no binary packages.
                fragments.add(fragment); // reuse fragments for class extraction
                packageNames.add(fragment.getElementName()); // add name to set.
            }
        }
        for (String name : packageNames) {
            currentModel.add(new ExtractedPackage(name)); // build model packages first
        }
        parseCompilationUnits(fragments); // then continue parsing
    }

    /**
     * Reports on the parsing progress by logging the current package.
     */
    private void reportProgress(int packages) {
        packageCounter++; // increase package count
        logger.info("Parsing package " + currentPackage.getFullName() + " (" + packageCounter + "/" + packages + ")");
        packageCounter = (packageCounter == packages) ? 0 : packageCounter; // reset to zero if finished
    }

    /**
     * Creates the {@link IntermediateModel} instance and the other parsers.
     */
    private void setup(IJavaProject project) {
        currentModel = new IntermediateModel(project.getElementName()); // create new model.
        dataTypeParser = new DataTypeExtractor();
        typeParser = new JavaTypeExtractor(currentModel, project, dataTypeParser);
        logger.info("Started parsing the project...");
    }
}