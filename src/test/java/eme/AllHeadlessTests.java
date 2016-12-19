package eme;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import eme.generator.EObjectGeneratorTest;
import eme.generator.EcoreMetamodelGeneratorTest;
import eme.model.IntermediateModelTest;

@RunWith(Suite.class)

@SuiteClasses({
    EcoreMetamodelGeneratorTest.class,
    EObjectGeneratorTest.class,
    IntermediateModelTest.class
})

public class AllHeadlessTests {
}
