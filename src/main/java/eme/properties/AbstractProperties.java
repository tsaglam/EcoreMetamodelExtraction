package eme.properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * The class is the super class for the class ExtractionProperties. It separates the loading, saving
 * and creating work flow of the properties file from the actual content in the ExtractionProperties
 * class.
 * @author Timur Saglam
 */
public abstract class AbstractProperties {
    private static String FILE_COMMENT;
    private static String FILE_NAME;
    protected final String path;
    protected Properties properties;

    /**
     * Basic constructor. Loads the properties file, creates a new one if the file does not exist.
     * @param name is the name of the properties file.
     * @param comment is the comment of the properties file.
     */
    public AbstractProperties(String name, String comment) { // TODO (HIGH) fix problem with path
        FILE_NAME = name;
        FILE_COMMENT = comment;
        path = System.getProperty("user.dir") + "/" + FILE_NAME; // create path.
        if (propertyFileExists()) {
            load(); // load if file exists.
        } else {
            create(); // create if not.
        }
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
     * Checks whether the properties file already exists (and is not a folder).
     * @return true if the file exists.
     */
    private boolean propertyFileExists() {
        File f = new File(path);
        return f.exists() && !f.isDirectory();
    }

    /**
     * creates new properties file.
     */
    protected void create() {
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
    protected void load() {
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
     * Sets all the properties to their default values.
     */
    abstract protected void setDefaultValues();
}
