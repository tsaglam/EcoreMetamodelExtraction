package eme.generator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EParameter;
import org.junit.Before;
import org.junit.Test;

import eme.model.ExtractedClass;
import eme.model.ExtractedEnumConstant;
import eme.model.ExtractedEnum;
import eme.model.ExtractedInterface;
import eme.model.ExtractedMethod;
import eme.model.ExtractedPackage;
import eme.model.ExtractedType;
import eme.model.IntermediateModel;
import eme.model.datatypes.AccessLevelModifier;
import eme.model.datatypes.ExtractedField;
import eme.model.datatypes.ExtractedDataType;
import eme.model.datatypes.ExtractedParameter;
import eme.properties.ExtractionProperties;
import eme.properties.TextProperty;

public class EPackageGeneratorTest {
    EPackageGenerator generator;
    IntermediateModel model;
    ExtractionProperties properties;

    @Before
    public void setUp() throws Exception {
        properties = new ExtractionProperties();
        model = new IntermediateModel("TestProject");
        generator = new EPackageGenerator(properties);
    }

    @Test
    public void testGenerateAbstractClass() {
        ExtractedClass abstractClass = new ExtractedClass("abstractClass", true, false);
        EClass result = (EClass) generateClassifier(abstractClass);
        assertEquals("abstractClass", result.getName());
        assertTrue(result.isAbstract());
        assertFalse(result.isInterface());
    }

    @Test
    public void testGenerateAttribute() {
        ExtractedClass testClass = new ExtractedClass("TestClass", false, false);
        testClass.addField(new ExtractedField("testAttribute", "java.lang.String", 0));
        EClass result = (EClass) generateClassifier(testClass);

        assertEquals(1, result.getEAttributes().size());
        EAttribute eAttribute = result.getEAttributes().get(0);
        assertEquals("testAttribute", eAttribute.getName());
        assertEquals("EString", eAttribute.getEType().getName());
    }

    @Test
    public void testGenerateClass() {
        ExtractedClass normalClass = new ExtractedClass("NormalClass", false, false);
        EClass result = (EClass) generateClassifier(normalClass);
        assertEquals("NormalClass", result.getName());
        assertFalse(result.isAbstract());
        assertFalse(result.isInterface());
    }

    @Test
    public void testGenerateCustomClassAttribute() {
        ExtractedClass testClass = new ExtractedClass("TestClass", false, false);
        testClass.addField(new ExtractedField("testAttribute", "TestClass2", 0));
        EClass result = (EClass) generateClassifier(testClass);
        assertEquals(1, result.getEAttributes().size());
        EAttribute eAttribute = result.getEAttributes().get(0);
        assertEquals("testAttribute", eAttribute.getName());
        assertEquals("TestClass2", eAttribute.getEType().getName());
    }

    @Test
    public void testGenerateEnum() {
        ExtractedEnum enumeration = new ExtractedEnum("Enum");
        for (int i = 0; i < 5; i++) {
            enumeration.addConstant(new ExtractedEnumConstant("ENUMERAL_" + i));
        }
        EEnum result = (EEnum) generateClassifier(enumeration);
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
        ExtractedClass testClass = new ExtractedClass("TestClass", false, false);
        testClass.addField(new ExtractedField("testAttribute", "main.view.External", 0));
        EClass result = (EClass) generateClassifier(testClass);
        assertEquals(1, result.getEAttributes().size());
        EAttribute eAttribute = result.getEAttributes().get(0);
        assertEquals("testAttribute", eAttribute.getName());
        assertEquals("External", eAttribute.getEType().getName());
    }

    @Test
    public void testGenerateIllegalSuper() {
        ExtractedClass subClass = new ExtractedClass("SubClass", false, false);
        subClass.setSuperClass(new ExtractedDataType("SuperClass", 0));
        subClass.addInterface(new ExtractedDataType("SuperInterface", 0));
        subClass.addInterface(new ExtractedDataType("SuperInterface2", 0));
        model.add(new ExtractedPackage(""));
        model.add(subClass);
        EClass result = (EClass) generateClassifier(subClass);
        assertEquals("SubClass", result.getName());
        assertEquals(0, result.getESuperTypes().size());
    }

    @Test
    public void testGenerateInterface() {
        ExtractedInterface extractedInterface = new ExtractedInterface("Interface");
        EClass result = (EClass) generateClassifier(extractedInterface);
        assertEquals("Interface", result.getName());
        assertTrue(result.isAbstract());
        assertTrue(result.isInterface());
    }

    @Test
    public void testGenerateMethod() {
        ExtractedClass testClass = new ExtractedClass("TestClass", false, false);
        ExtractedDataType returnType = new ExtractedDataType("java.lang.String", 0);
        ExtractedMethod method = new ExtractedMethod("testMethod", returnType);
        method.setModifier(AccessLevelModifier.PUBLIC);
        method.addParameter(new ExtractedParameter("number", "int", 0));
        testClass.addMethod(method);
        EClass result = (EClass) generateClassifier(testClass);
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
        model.add(new ExtractedPackage(""));
        model.add(new ExtractedPackage("model"));
        model.add(new ExtractedPackage("view"));
        model.add(new ExtractedPackage("controller"));
        model.add(new ExtractedEnum("someEnum"));
        EPackage result = generator.generate(model);
        assertNotNull(result);
        assertEquals("DEFAULT", result.getName());
        List<EPackage> subpackages = result.getESubpackages();
        assertEquals(4, subpackages.size());
        assertEquals(properties.get(TextProperty.DATATYPE_PACKAGE), subpackages.get(0).getName());
        assertEquals("model", subpackages.get(1).getName());
        assertEquals("view", subpackages.get(2).getName());
        assertEquals("controller", subpackages.get(3).getName());
        for (EPackage ePackage : subpackages) {
            assertEquals(ePackage.getName(), ePackage.getNsPrefix());
        }
    }

    @Test
    public void testGenerateSuperClass() {
        ExtractedClass sub = new ExtractedClass("SubClass", false, false);
        ExtractedClass sub2 = new ExtractedClass("SubClass2", false, false);
        ExtractedClass superType = new ExtractedClass("SuperClass", false, false);
        sub.setSuperClass(new ExtractedDataType("SuperClass", 0));
        sub2.setSuperClass(new ExtractedDataType("SuperClass", 0));
        EPackage root = generateClassifiers(superType, sub, sub2);
        EClass result = (EClass) root.getEClassifier(sub.getName());
        EClass result2 = (EClass) root.getEClassifier(sub2.getName());
        assertEquals("SubClass", result.getName());
        assertEquals("SuperClass", result.getESuperTypes().get(0).getName());
        assertEquals("SubClass2", result2.getName());
        assertEquals("SuperClass", result2.getESuperTypes().get(0).getName());
    }

    @Test
    public void testGenerateSuperInterface() {
        ExtractedInterface sub = new ExtractedInterface("SubInterface");
        ExtractedInterface sub2 = new ExtractedInterface("SubInterface2");
        ExtractedInterface superType = new ExtractedInterface("SuperInterface");
        sub.addInterface(new ExtractedDataType("SuperInterface", 0));
        sub2.addInterface(new ExtractedDataType("SuperInterface", 0));
        EPackage root = generateClassifiers(superType, sub, sub2);
        EClass result = (EClass) root.getEClassifier(sub.getName());
        EClass result2 = (EClass) root.getEClassifier(sub2.getName());
        assertEquals("SubInterface", result.getName());
        assertEquals("SuperInterface", result.getESuperTypes().get(0).getName());
        assertEquals("SubInterface2", result2.getName());
        assertEquals("SuperInterface", result2.getESuperTypes().get(0).getName());
    }

    private EClassifier generateClassifier(ExtractedType type) {
        model.add(new ExtractedPackage("")); // set root
        model.add(type); // add type
        EPackage root = generator.generate(model); // generate
        return root.getEClassifier(type.getName());
    }

    private EPackage generateClassifiers(ExtractedType... types) {
        model.add(new ExtractedPackage("")); // set root
        for (ExtractedType type : types) {
            model.add(type); // add types
        }
        return generator.generate(model); // generate
    }
}