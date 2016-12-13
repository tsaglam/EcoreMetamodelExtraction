package eme;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * This class manages the extraction properties in the user.properties file.
 * @author Timur Saglam
 */
public class ExtractionProperties { // TODO (MEDIUM) add setters for properties.
    private static final String FILE_COMMENT = "Use this file to configure the Ecore metamodel extraction.";
    private static final String FILE_NAME = "user.properties";
    public static void main(String[] args) {
        new ExtractionProperties(); // TODO (LOW) remove main method.
    }
    private final String path;

    private Properties properties;

    /**
     * Basic constructor. Loads the properties file, creates a new one if the file does not exist.
     */
    public ExtractionProperties() { // TODO (HIGH) fix problem with path when ran as plugin
        path = System.getProperty("user.dir") + "/" + FILE_NAME; // create path.
        if (propertyFileExists()) {
            load(); // load if file exists.
        } else {
            create(); // create if not.
        }
    }

    /**
     * Returns the value of the property DefaultPackageName.
     * @return the value.
     */
    public String getDefaultPackageName() {
        return properties.getProperty("DefaultPackageName");
    }

    /**
     * Returns the value of the property ExtractAbstractMethods.
     * @return the value.
     */
    public boolean getExtractAbstractMethods() {
        return Boolean.parseBoolean(properties.getProperty("ExtractAbstractMethods"));
    }

    /**
     * Returns the value of the property ExtractEmptyPackages.
     * @return the value.
     */
    public boolean getExtractEmptyPackages() {
        return Boolean.parseBoolean(properties.getProperty("ExtractEmptyPackages"));
    }

    /**
     * Returns the value of the property ExtractNestedTypes.
     * @return the value.
     */
    public boolean getExtractNestedTypes() {
        return Boolean.parseBoolean(properties.getProperty("ExtractNestedTypes"));
    }

    /**
     * Returns the value of the property ExtractStaticAttributes.
     * @return the value.
     */
    public boolean getExtractStaticAttributes() {
        return Boolean.parseBoolean(properties.getProperty("ExtractStaticAttributes"));
    }

    /**
     * Returns the value of the property ExtractStaticMethods.
     * @return the value.
     */
    public boolean getExtractStaticMethods() {
        return Boolean.parseBoolean(properties.getProperty("ExtractStaticMethods"));
    }

    /**
     * Returns the value of the property SavingStrategy.
     * @return the value.
     */
    public String getSavingStrategy() {
        return properties.getProperty("SavingStrategy");
    }

    /**
     * Saves the settings to the properties file.
     */
    public void save() {
        try {
            FileOutputStream out = new FileOutputStream(path); // create output stream
            properties.store(out, FILE_COMMENT); // store with stream
            out.close(); // close stream
        } catch (FileNotFoundException exception) {
            exception.printStackTrace();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * creates new properties file.
     */
    private void create() {
        File file = new File(path); // create file object
        file.getParentFile().mkdirs(); // be sure that the parent directory exists
        try {
            file.createNewFile(); // write file to disk
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        load(); // then load the properties file.
        setDefaultValues(); // set defaults.
        save(); // save to file.
    }

    /**
     * Loads the settings from the properties file.
     */
    private void load() {
        try {
            properties = new Properties(); // create properties object
            FileInputStream in = new FileInputStream(path); // create input stream
            properties.load(in); // load from stream
            in.close(); // close stream
        } catch (FileNotFoundException exception) {
            exception.printStackTrace();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Checks whether the properties file already exists (and is not a folder).
     * @return true if the file exists.
     */
    private boolean propertyFileExists() {
        File f = new File(path);
        return f.exists() && !f.isDirectory();
    }

    /**
     * Sets all the properties to their default values.
     */
    private void setDefaultValues() {
        properties.setProperty("DefaultPackageName", "DEFAULT");
        properties.setProperty("ExtractEmptyPackages", "true");
        properties.setProperty("ExtractNestedTypes", "true");
        properties.setProperty("ExtractAbstractMethods", "false");
        properties.setProperty("ExtractStaticMethods", "false");
        properties.setProperty("ExtractStaticAttributes", "false");
        properties.setProperty("SavingStrategy", "OutputProject");
    }
}