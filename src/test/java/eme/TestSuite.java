package eme;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import eme.generator.EPackageGeneratorTest;
import eme.generator.EcoreMetamodelGeneratorTest;
import eme.model.IntermediateModelTest;

@RunWith(Suite.class)

@SuiteClasses({
    EcoreMetamodelGeneratorTest.class,
    EPackageGeneratorTest.class,
    IntermediateModelTest.class
})

public class TestSuite {
}
