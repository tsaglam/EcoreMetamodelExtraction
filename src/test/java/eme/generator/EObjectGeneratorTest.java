package eme.generator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.junit.Before;
import org.junit.Test;

import eme.model.ExtractedClass;
import eme.model.ExtractedEnumeration;
import eme.model.ExtractedInterface;
import eme.model.ExtractedPackage;
import eme.properties.ExtractionProperties;
import eme.properties.TestProperties;

public class EObjectGeneratorTest {
    ExtractionProperties properties;
    EObjectGenerator generator;

    @Before
    public void setUp() throws Exception {
        properties = new TestProperties();
        generator = new EObjectGenerator(properties);
    }

    @Test
    public void testGeneratePackage() {
        ExtractedPackage root = new ExtractedPackage("");
        root.setAsRoot();
        root.add(new ExtractedPackage("model"));
        root.add(new ExtractedPackage("view"));
        root.add(new ExtractedPackage("controller"));
        root.add(new ExtractedEnumeration("someEnum"));
        EPackage result = generator.generateEPackage(root, "testGeneratePackage");
        assertNotNull(result);
        assertEquals("DEFAULT", result.getName());
        List<EPackage> subpackages = result.getESubpackages();
        assertEquals(3, subpackages.size());
        assertEquals("model", subpackages.get(0).getName());
        assertEquals("view", subpackages.get(1).getName());
        assertEquals("controller", subpackages.get(2).getName());
        for (EPackage ePackage : subpackages) {
            assertEquals(ePackage.getName(), ePackage.getNsPrefix());
        }
    }

    @Test
    public void testGenerateClass() {
        ExtractedClass normalClass = new ExtractedClass("normalClass", false);
        EClass result = (EClass) generator.generateEClassifier(normalClass);
        assertEquals("normalClass", result.getName());
        assertFalse(result.isAbstract());
        assertFalse(result.isInterface());
    }

    @Test
    public void testGenerateAbstractClass() {
        ExtractedClass abstractClass = new ExtractedClass("abstractClass", true);
        EClass result = (EClass) generator.generateEClassifier(abstractClass);
        assertEquals("abstractClass", result.getName());
        assertTrue(result.isAbstract());
        assertFalse(result.isInterface());
    }

    @Test
    public void testGenerateInterface() {
        ExtractedInterface extractedInterface = new ExtractedInterface("interface");
        EClass result = (EClass) generator.generateEClassifier(extractedInterface);
        assertEquals("interface", result.getName());
        assertTrue(result.isAbstract());
        assertTrue(result.isInterface());
    }

    @Test
    public void testGenerateEnum() {
        ExtractedEnumeration enumeration = new ExtractedEnumeration("enum");
        for (int i = 0; i < 5; i++) {
            enumeration.addEnumeral("ENUMERAL_" + i);
        }
        EEnum result = (EEnum) generator.generateEClassifier(enumeration);
        assertEquals("enum", result.getName());
        for (int i = 0; i < 5; i++) {
            assertNotNull(result.getEEnumLiteral("ENUMERAL_" + i));
        }
    }

}
