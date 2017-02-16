package eme.generator.saving;

import java.io.File;

/**
 * Container class that contains information about a saved Ecore metamodel.
 * @author Timur Saglam
 */
public class SavingInformation {
    final private String fileName;
    final private String filePath;
    final private String projectName;

    /**
     * Basic constructor that sets the information.
     * @param filePath is the path where the Ecore file was saved.
     * @param fileName is the name of file where the Ecore metamodel was saved in.
     */
    public SavingInformation(String filePath, String fileName) {
        this.filePath = filePath;
        this.fileName = fileName;
        String[] segments = filePath.split(File.separator);
        projectName = segments[segments.length - 2];
    }

    /**
     * Accessor for the file name.
     * @return the name of file where the Ecore metamodel was saved in.
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Accessor for the file path.
     * @return the file path where the Ecore file was saved.
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * Accessor for the project name.
     * @return the the name of the saved project.
     */
    public String getProjectName() {
        return projectName;
    }
}