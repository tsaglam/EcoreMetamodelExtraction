package eme.codegen;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.emf.codegen.ecore.generator.Generator;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.codegen.ecore.genmodel.generator.GenBaseGeneratorAdapter;

/**
 * Class for code generation (e.g generating Java code from Ecore GenModels).
 */
public final class CodeGenerator {
    private static final Logger logger = LogManager.getLogger(CodeGenerator.class.getName());

    /**
     * Uses a specific GenModel to generate Java Code.
     * @param genModel is the specific GenModel.
     */
    public static void generate(GenModel genModel) {
        if (genModel == null) {
            throw new IllegalArgumentException("GenModel cannot be null to generate code from it");
        }
        genModel.setCanGenerate(true); // allow generation
        Generator generator = new Generator(); // create generator
        generator.setInput(genModel); // set the model-level input object
        generator.generate(genModel, GenBaseGeneratorAdapter.MODEL_PROJECT_TYPE, new MonitorToLoggerAdapter(logger));
        logger.info("Generated Java code from GenModel in: " + generator.getGeneratedOutputs().toString());
    }
}