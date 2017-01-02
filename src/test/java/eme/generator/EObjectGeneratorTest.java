package eme.generator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EParameter;
import org.junit.Before;
import org.junit.Test;

import eme.model.ExtractedClass;
import eme.model.ExtractedEnumeration;
import eme.model.ExtractedInterface;
import eme.model.ExtractedMethod;
import eme.model.ExtractedPackage;
import eme.model.IntermediateModel;
import eme.model.datatypes.AccessLevelModifier;
import eme.model.datatypes.ExtractedAttribute;
import eme.model.datatypes.ExtractedDataType;
import eme.model.datatypes.ExtractedParameter;
import eme.properties.ExtractionProperties;
import eme.properties.TestProperties;

public class EObjectGeneratorTest {
    EObjectGenerator generator;
    IntermediateModel model;
    ExtractionProperties properties;

    @Before
    public void setUp() throws Exception {
        properties = new TestProperties();
        generator = new EObjectGenerator(properties);
        model = new IntermediateModel("TestProject");
        generator.prepareFor(model);
    }

    @Test
    public void testGenerateAbstractClass() {
        ExtractedClass abstractClass = new ExtractedClass("abstractClass", true);
        EClass result = (EClass) generator.generateEClassifier(abstractClass);
        generator.completeGeneration();
        assertEquals("abstractClass", result.getName());
        assertTrue(result.isAbstract());
        assertFalse(result.isInterface());
    }

    @Test
    public void testGenerateAttribute() {
        ExtractedClass testClass = new ExtractedClass("TestClass", false);
        ExtractedAttribute attribute = new ExtractedAttribute("testAttribute", "java.lang.String", 0);
        testClass.addAttribute(attribute);
        EClass result = (EClass) generator.generateEClassifier(testClass);
        generator.completeGeneration();
        assertEquals(1, result.getEAttributes().size());
        EAttribute eAttribute = result.getEAttributes().get(0);
        assertEquals("testAttribute", eAttribute.getName());
        assertEquals("EString", eAttribute.getEType().getName());
    }

    @Test
    public void testGenerateClass() {
        ExtractedClass normalClass = new ExtractedClass("NormalClass", false);
        EClass result = (EClass) generator.generateEClassifier(normalClass);
        generator.completeGeneration();
        assertEquals("NormalClass", result.getName());
        assertFalse(result.isAbstract());
        assertFalse(result.isInterface());
    }

    @Test
    public void testGenerateEnum() {
        ExtractedEnumeration enumeration = new ExtractedEnumeration("Enum");
        for (int i = 0; i < 5; i++) {
            enumeration.addEnumeral("ENUMERAL_" + i);
        }
        EEnum result = (EEnum) generator.generateEClassifier(enumeration);
        generator.completeGeneration();
        assertEquals("Enum", result.getName());
        for (int i = 0; i < 5; i++) {
            assertNotNull(result.getEEnumLiteral("ENUMERAL_" + i));
        }
    }

    @Test
    public void testGenerateExternalAttribute() {
        ExtractedPackage root = new ExtractedPackage("");
        root.setAsRoot();
        model.add(root);
        ExtractedClass testClass = new ExtractedClass("TestClass", false);
        ExtractedAttribute attribute = new ExtractedAttribute("testAttribute", "main.view.External", 0);
        testClass.addAttribute(attribute);
        generator.generateEPackage(root);
        EClass result = (EClass) generator.generateEClassifier(testClass);
        generator.completeGeneration();
        assertEquals(1, result.getEAttributes().size());
        EAttribute eAttribute = result.getEAttributes().get(0);
        assertEquals("testAttribute", eAttribute.getName());
        assertEquals("External", eAttribute.getEType().getName());
    }

    @Test
    public void testGenerateIllegalSuper() {
        ExtractedClass subClass = new ExtractedClass("SubClass", false);
        subClass.setSuperClass("SuperClass");
        subClass.addInterface("SuperInterface");
        subClass.addInterface("SuperInterface2");
        model.add(new ExtractedPackage(""));
        model.add(subClass);
        EClass result = (EClass) generator.generateEClassifier(subClass);
        generator.completeGeneration();
        assertEquals("SubClass", result.getName());
        assertEquals(0, result.getESuperTypes().size());
    }

    @Test
    public void testGenerateInterface() {
        ExtractedInterface extractedInterface = new ExtractedInterface("Interface");
        EClass result = (EClass) generator.generateEClassifier(extractedInterface);
        generator.completeGeneration();
        assertEquals("Interface", result.getName());
        assertTrue(result.isAbstract());
        assertTrue(result.isInterface());
    }

    @Test
    public void testGenerateMethod() {
        ExtractedClass testClass = new ExtractedClass("TestClass", false);
        ExtractedDataType returnType = new ExtractedDataType("java.lang.String", 0);
        ExtractedParameter parameter = new ExtractedParameter("number", "int", 0);
        ExtractedMethod method = new ExtractedMethod("testMethod", returnType, false);
        method.setFlags(AccessLevelModifier.PUBLIC, false, false);
        method.addParameter(parameter);
        testClass.addMethod(method);
        EClass result = (EClass) generator.generateEClassifier(testClass);
        generator.completeGeneration();
        assertEquals(1, result.getEOperations().size());
        EOperation operation = result.getEOperations().get(0);
        assertEquals("testMethod", operation.getName());
        assertEquals("EString", operation.getEType().getName());
        EParameter eParameter = operation.getEParameters().get(0);
        assertEquals("number", eParameter.getName());
        assertEquals("EInt", eParameter.getEType().getName());
    }

    @Test
    public void testGeneratePackage() {
        ExtractedPackage root = new ExtractedPackage("");
        root.setAsRoot();
        root.add(new ExtractedPackage("model"));
        root.add(new ExtractedPackage("view"));
        root.add(new ExtractedPackage("controller"));
        root.add(new ExtractedEnumeration("someEnum"));
        EPackage result = generator.generateEPackage(root);
        generator.completeGeneration();
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
    public void testGenerateSuperClass() {
        ExtractedClass subClass = new ExtractedClass("SubClass", false);
        ExtractedClass subClass2 = new ExtractedClass("SubClass2", false);
        ExtractedClass superClass = new ExtractedClass("SuperClass", false);
        subClass.setSuperClass("SuperClass");
        subClass2.setSuperClass("SuperClass");
        model.add(new ExtractedPackage(""));
        model.add(superClass);
        EClass result = (EClass) generator.generateEClassifier(subClass);
        EClass result2 = (EClass) generator.generateEClassifier(subClass2);
        generator.completeGeneration();
        assertEquals("SubClass", result.getName());
        assertEquals("SuperClass", result.getESuperTypes().get(0).getName());
        assertEquals("SubClass2", result2.getName());
        assertEquals("SuperClass", result2.getESuperTypes().get(0).getName());
    }

    @Test
    public void testGenerateSuperInterface() {
        ExtractedInterface subInterface = new ExtractedInterface("SubInterface");
        ExtractedInterface subInterface2 = new ExtractedInterface("SubInterface2");
        ExtractedInterface superInterface = new ExtractedInterface("SuperInterface");
        subInterface.addInterface("SuperInterface");
        subInterface2.addInterface("SuperInterface");
        model.add(new ExtractedPackage(""));
        model.add(superInterface);
        EClass result = (EClass) generator.generateEClassifier(subInterface);
        EClass result2 = (EClass) generator.generateEClassifier(subInterface2);
        generator.completeGeneration();
        assertEquals("SubInterface", result.getName());
        assertEquals("SuperInterface", result.getESuperTypes().get(0).getName());
        assertEquals("SubInterface2", result2.getName());
        assertEquals("SuperInterface", result2.getESuperTypes().get(0).getName());
    }
}