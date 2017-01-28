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

/**
 * This class manages the extraction properties in the user.properties file.
 * @author Timur Saglam
 */
public class ExtractionProperties {
    private static final String FILE_COMMENT = "Use this file to configure the Ecore metamodel extraction.";
    private static final String FILE_NAME = "user.properties";
    private static final Logger logger = LogManager.getLogger(ExtractionProperties.class.getName());
    private URL fileURL;
    private Properties properties;

    /**
     * Basic constructor. Loads the properties file, creates a new one if the file does not exist.
     */
    public ExtractionProperties() {
        Bundle bundle = Platform.getBundle("EcoreMetamodelExtraction");
        Path path = new Path(FILE_NAME);
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
     * Accessor method for binary properties.
     * @param property is the {@link BinaryProperty}.
     * @return the boolean value of the property.
     */
    public boolean get(BinaryProperty property) {
        return Boolean.parseBoolean(properties.getProperty(property.getKey(), property.getDefaultValue()));
    }

    /**
     * Accessor method for text properties.
     * @param property is the {@link TextProperty}.
     * @return the String value of the property.
     */
    public String get(TextProperty property) {
        return properties.getProperty(property.getKey(), property.getDefaultValue());
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
            logger.error(exception);
        } catch (IOException exception) {
            logger.error(exception);
        }
    }

    /**
     * Mutator method for binary properties.
     * @param property is the {@link TextProperty}.
     * @param value is the String value to set.
     */
    public void set(BinaryProperty property, boolean value) {
        properties.setProperty(property.getKey(), Boolean.toString(value));

    }

    /**
     * Mutator method for text properties.
     * @param property is the {@link BinaryProperty}.
     * @param value is the boolean value to set.
     */
    public void set(TextProperty property, String value) {
        properties.setProperty(property.getKey(), value);
    }

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