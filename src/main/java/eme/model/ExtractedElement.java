package eme.model;

/**
 * Superclass of all extracted elements of the intermediate model. The intermediate model is the
 * temporary model between the implicit model of the code files and the Ecore metamodel.
 * @author Timur Saglam
 */
public abstract class ExtractedElement {

    /**
     * Calculates the index of the separator of a full qualified name.
     * @param fullName is the full qualified name.
     * @return the index of the separator.
     */
    private int separator(String fullName) {
        return fullName.lastIndexOf('.');
    }

    /**
     * Creates the name of a class or package from a full qualified name with both path and name.
     * @param fullName is the full qualified name.
     * @return the name.
     */
    protected String createName(String fullName) {
        if (!fullName.contains(".")) {
            return fullName; // already is name.
        } else { // split name from parent package:
            return fullName.substring(separator(fullName) + 1); // get name
        }
    }

    /**
     * Creates the path of a class or package from a full qualified name with both path and name.
     * @param fullName is the full qualified name.
     * @return the path.
     */
    protected String createPath(String fullName) {
        if (!fullName.contains(".")) {
            return ""; // has no parent.
        } else { // split name from parent package:
            return fullName.substring(0, separator(fullName)); // get path
        }
    }
}
