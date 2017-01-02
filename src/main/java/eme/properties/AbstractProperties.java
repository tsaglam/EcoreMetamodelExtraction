package eme.properties;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Properties;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import eme.parser.JavaProjectParser;

/**
 * The class is the super class for the class ExtractionProperties. It separates the loading, saving and creating
 * process of the properties file from the actual getters and setters in the ExtractionProperties class. This class also
 * enables the simple creation of additional property classes by deriving a class from this one.
 * @author Timur Saglam
 */
public abstract class AbstractProperties {
    private static final Logger logger = LogManager.getLogger(JavaProjectParser.class.getName());
    private final String fileComment;
    private URL fileURL;
    protected Properties properties;

    /**
     * Basic constructor. Loads the properties file, creates a new one if the file does not exist.
     * @param fileName is the name of the properties file.
     * @param fileComment is the comment of the properties file.
     */
    public AbstractProperties(String fileName, String fileComment) {
        this.fileComment = fileComment;
        Bundle bundle = Platform.getBundle("EcoreMetamodelExtraction");
        Path path = new Path(fileName);
        try {
            fileURL = FileLocator.find(bundle, path, null);
            load(); // load if file exists.
        } catch (NoClassDefFoundError error) {
            logger.warn("Could not reach properties file.", error);
            properties = new Properties(); // create dummy properties.
        } catch (ExceptionInInitializerError error) {
            logger.warn("Could not reach properties file.", error);
            properties = new Properties(); // create dummy properties.
        }
    }

    /**
     * Saves the settings to the properties file.
     */
    public void save() {
        try {
            OutputStream out = fileURL.openConnection().getOutputStream(); // create output stream
            properties.store(out, fileComment); // store with stream
            out.close(); // close stream
        } catch (FileNotFoundException exception) {
            logger.error(exception);
        } catch (IOException exception) {
            logger.error(exception);
        }
    }

    /**
     * Sets all the properties to their default values.
     */
    public abstract void setDefaultValues();

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
            logger.error(exception);
        } catch (IOException exception) {
            logger.error(exception);
        }
    }
}