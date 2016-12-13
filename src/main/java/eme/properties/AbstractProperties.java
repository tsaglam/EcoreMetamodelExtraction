package eme.properties;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Properties;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

/**
 * The class is the super class for the class ExtractionProperties. It separates the loading, saving
 * and creating work flow of the properties file from the actual content in the ExtractionProperties
 * class.
 * @author Timur Saglam
 */
public abstract class AbstractProperties {
    private static String FILE_COMMENT;
    private final URL fileURL;
    protected Properties properties;

    /**
     * Basic constructor. Loads the properties file, creates a new one if the file does not exist.
     * @param name is the name of the properties file.
     * @param comment is the comment of the properties file.
     */
    public AbstractProperties(String name, String comment) {
        FILE_COMMENT = comment;
        Bundle bundle = Platform.getBundle("EcoreMetamodelExtraction");
        Path path = new Path(name);
        fileURL = FileLocator.find(bundle, path, null);
        load(); // load if file exists.
    }

    /**
     * Saves the settings to the properties file.
     */
    public void save() {
        try {
            OutputStream out = fileURL.openConnection().getOutputStream(); // create output stream
            properties.store(out, FILE_COMMENT); // store with stream
            out.close(); // close stream
        } catch (FileNotFoundException exception) {
            exception.printStackTrace();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Sets all the properties to their default values.
     */
    abstract public void setDefaultValues();

    /**
     * Loads the settings from the properties file.
     */
    private void load() {
        try {
            properties = new Properties(); // create properties object
            InputStream in = fileURL.openStream(); // create input stream
            properties.load(in); // load from stream
            in.close(); // close stream
        } catch (FileNotFoundException exception) {
            exception.printStackTrace();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}