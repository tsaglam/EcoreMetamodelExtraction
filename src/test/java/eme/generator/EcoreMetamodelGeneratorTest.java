package eme.generator;

import static org.junit.Assert.assertEquals;

import org.eclipse.emf.ecore.EPackage;
import org.junit.Before;
import org.junit.Test;

import eme.model.ExtractedPackage;
import eme.model.IntermediateModel;
import eme.properties.ExtractionProperties;
import eme.properties.TextProperty;

public class EcoreMetamodelGeneratorTest {
    EcoreMetamodelGenerator generator;
    IntermediateModel model;
    ExtractionProperties properties;

    @Before
    public void setUp() throws Exception {
        properties = new ExtractionProperties(); // don't use real properties
        generator = new EcoreMetamodelGenerator(properties);
        model = new IntermediateModel("UnitTestProject");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalModel() {
        generator.generateMetamodel(model); // empty model
    }

    @Test(expected = IllegalStateException.class)
    public void testNullSaving() {
        generator.saveMetamodel(); // no metamodel
    }

    @Test
    public void testPackageStructure() {
        buildMVCPackages();
        EPackage root = generator.generateMetamodel(model).getRoot();
        assertEquals(properties.get(TextProperty.DEFAULT_PACKAGE), root.getName());
        assertEquals(properties.get(TextProperty.DEFAULT_PACKAGE), root.getNsPrefix());
        assertEquals(model.getProjectName() + "/", root.getNsURI());
        assertEquals(2, root.getESubpackages().size());
        EPackage main = root.getESubpackages().get(1);
        assertEquals(3, main.getESubpackages().size());
    }

    private void buildMVCPackages() {
        model.add(new ExtractedPackage(""));
        model.add(new ExtractedPackage("main"));
        model.add(new ExtractedPackage("main.model"));
        model.add(new ExtractedPackage("main.view"));
        model.add(new ExtractedPackage("main.controller"));
    }
}
