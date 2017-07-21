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
 * Abstract class for the management of property files with any {@link ITextProperty} and {@link IBinaryProperty}.
 * @author Timur Saglam
 * @param <T> is the text property enumeration.
 * @param <B> is the binary property enumeration.
 */
public class AbstractProperties<T extends ITextProperty, B extends IBinaryProperty> {
    private static final Logger logger = LogManager.getLogger(AbstractProperties.class.getName());
    private String fileComment;
    private URL fileURL;
    private Properties properties;

    /**
     * Basic constructor. Loads the properties file, creates a new one if the file does not exist.
     * @param fileName is the name of the property file.
     * @param fileComment is the description in the property file.
     * @param bundleName is the symbolic name of the {@link Bundle}.
     */
    public AbstractProperties(String fileName, String fileComment, String bundleName) {
        this.fileComment = fileComment;
        Bundle bundle = Platform.getBundle(bundleName);
        if (bundle == null) {
            throw new IllegalArgumentException(
                    getClass().getSimpleName() + " could not be initialized because the bundle " + bundleName + " was not found");
        }
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
     * Accessor method for binary properties.
     * @param property is the {@link IBinaryProperty}.
     * @return the boolean value of the property.
     */
    public boolean get(B property) {
        return Boolean.parseBoolean(properties.getProperty(property.getKey(), Boolean.toString(property.getDefaultValue())));
    }

    /**
     * Accessor method for text properties.
     * @param property is the {@link ITextProperty}.
     * @return the String value of the property.
     */
    public String get(T property) {
        return properties.getProperty(property.getKey(), property.getDefaultValue());
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
     * Mutator method for binary properties.
     * @param property is the {@link ITextProperty}.
     * @param value is the String value to set.
     */
    public void set(B property, boolean value) {
        properties.setProperty(property.getKey(), Boolean.toString(value));

    }

    /**
     * Mutator method for text properties.
     * @param property is the {@link IBinaryProperty}.
     * @param value is the boolean value to set.
     */
    public void set(T property, String value) {
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