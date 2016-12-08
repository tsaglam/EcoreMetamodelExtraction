package eme.generator;

/**
 * This class creates and manages an EMF project. The project is important as an target for the
 * genrated ecore file.
 * @author Timur Saglam
 */
public class EMFProjectGenerator { // TODO (HIGH) implement the class EMFProjectGenerator.
    private String projectName;

    /**
     * Basic constructor.
     * @param projectName is the name of the project.
     */
    public EMFProjectGenerator(String projectName) {
        this.projectName = projectName;
    }

}
