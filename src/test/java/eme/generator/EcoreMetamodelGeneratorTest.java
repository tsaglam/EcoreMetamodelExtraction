package eme.generator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import eme.model.ExtractedEnumeration;
import eme.model.ExtractedPackage;
import eme.model.IntermediateModel;
import eme.properties.ExtractionProperties;
import eme.properties.TestProperties;

public class EcoreMetamodelGeneratorTest {
    ExtractionProperties properties;
    EcoreMetamodelGenerator generator;
    IntermediateModel model;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        properties = new TestProperties(); // don't use real properties
        generator = new EcoreMetamodelGenerator(properties);
        model = new IntermediateModel("UnitTestProject");
    }

    @Test
    public void testPackages() {
        buildMVCPackages();
        EPackage metamodel = generator.generateMetamodelFrom(model);
        assertEquals(properties.getDefaultPackageName(), metamodel.getName());
        assertEquals(properties.getDefaultPackageName(), metamodel.getNsPrefix());
        assertEquals(model.getProjectName() + "/", metamodel.getNsURI());
        assertEquals(1, metamodel.getESubpackages().size());
        EPackage main = metamodel.getESubpackages().get(0);
        assertEquals(3, main.getESubpackages().size());
    }

    @Test
    public void testEnums() {
        buildEnum();
        EPackage metamodel = generator.generateMetamodelFrom(model);
        assertEquals(1, metamodel.getEClassifiers().size());
        EEnum enumeration = (EEnum) metamodel.getEClassifiers().get(0);
        assertEquals("SomeEnum", enumeration.getName());
        assertEquals(3, enumeration.getELiterals().size());
        assertNotNull(enumeration.getEEnumLiteral("ONE"));
        assertNotNull(enumeration.getEEnumLiteral("TWO"));
        assertNotNull(enumeration.getEEnumLiteral("THREE"));
    }

    @Test(expected = IllegalStateException.class)
    public void testNullSaving() {
        generator.saveMetamodel(); // no metamodel
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalModel() {
        generator.generateMetamodelFrom(model); // empty model
    }

    private void buildMVCPackages() {
        model.add(new ExtractedPackage(""));
        model.add(new ExtractedPackage("main"));
        model.add(new ExtractedPackage("main.model"));
        model.add(new ExtractedPackage("main.view"));
        model.add(new ExtractedPackage("main.controller"));
    }

    private void buildEnum() {
        ExtractedPackage extractedPackage = new ExtractedPackage("");
        ExtractedEnumeration enumeration = new ExtractedEnumeration("SomeEnum");
        enumeration.addEnumeral("ONE");
        enumeration.addEnumeral("TWO");
        enumeration.addEnumeral("THREE");
        extractedPackage.add(enumeration);
        model.add(extractedPackage);
        model.add(enumeration);
    }
}
