package eme.model;

/**
 * Superclass of all extracted elements of the intermediate model. The intermediate model is the
 * temporary model between the implicit model of the code files and the Ecore metamodel.
 * @author Timur Saglam
 */
public abstract class ExtractedElement {
    protected String name;
    protected String parent;
    protected boolean selected; // selection for saving.

    /**
     * Basic constructor which extracts the name and the parents name from the full name.
     * @param fullName is the full name.
     */
    public ExtractedElement(String fullName) {
        name = createName(fullName);
        parent = createPath(fullName);
        selected = true;
    }

    /**
     * Getter for the full element name.
     * @return the full name of the element, consisting out of the package path and the element name
     * separated by an dot.
     */
    public String getFullName() {
        if (parent.equals("")) {
            return name;
        }
        return parent + "." + name;
    }

    /**
     * Getter for the element name.
     * @return the element name.
     */
    public String getName() {
        return name;
    }

    /**
     * Getter for the name of the elements parent.
     * @return the parent name.
     */
    public String getParentName() {
        return parent;
    }

    /**
     * Checks whether the element is selected.
     * @return true if selected.
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Sets whether is element is selected or not.
     * @param selected true to select the element, false to unselect.
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public String toString() {
        return name;
    }

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
