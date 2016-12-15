package eme.properties;

import eme.properties.ExtractionProperties;

/**
 * This class mocks a the class ExtractionProperties. It stores all values locally
 * @author Timur Saglam
 */
public class TestProperties extends ExtractionProperties {

    String defaultPackageName;
    boolean extractAbstractMethods;
    boolean extractEmptyPackages;
    boolean extractNestedTypes;
    boolean extractStaticAttributes;
    boolean extractStaticMethods;
    String savingStrategy;

    public TestProperties() {
        defaultPackageName = "DEFAULT";
        extractEmptyPackages = true;
        extractNestedTypes = true;
        extractAbstractMethods = false;
        extractStaticMethods = false;
        extractStaticAttributes = false;
        savingStrategy = "NewProject";
    }

    @Override
    public String getDefaultPackageName() {
        return defaultPackageName;
    }

    @Override
    public boolean getExtractAbstractMethods() {
        return extractAbstractMethods;
    }

    @Override
    public boolean getExtractEmptyPackages() {
        return extractEmptyPackages;
    }

    @Override
    public boolean getExtractNestedTypes() {
        return extractNestedTypes;
    }

    @Override
    public boolean getExtractStaticAttributes() {
        return extractStaticAttributes;
    }

    @Override
    public boolean getExtractStaticMethods() {
        return extractStaticMethods;
    }

    @Override
    public String getSavingStrategy() {
        return savingStrategy;
    }

    @Override
    public void setDefaultPackageName(String value) {
        defaultPackageName = value;
    }

    @Override
    public void setExtractAbstractMethods(boolean value) {
        extractAbstractMethods = value;
    }

    @Override
    public void setExtractEmptyPackages(boolean value) {
        extractEmptyPackages = value;
    }

    @Override
    public void setExtractNestedTypes(boolean value) {
        extractNestedTypes = value;
    }

    @Override
    public void setExtractStaticAttributes(boolean value) {
        extractStaticAttributes = value;
    }

    @Override
    public void setExtractStaticMethods(boolean value) {
        extractStaticMethods = value;
    }

    @Override
    public void setSavingStrategy(String value) {
        savingStrategy = value;
    }
}
