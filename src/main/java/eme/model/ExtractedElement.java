package eme.model;

/**
 * Superclass of all extracted elements of an {@link IntermediateModel}.
 * @author Timur Saglam
 */
public abstract class ExtractedElement implements Comparable<ExtractedElement> {
    protected String name;
    protected String parent;
    protected boolean selected; // selection for saving.

    /**
     * Basic constructor which extracts the name and the parents name from the full
     * name.
     * @param fullName is the full name.
     */
    public ExtractedElement(String fullName) {
        name = createName(fullName);
        parent = createPath(fullName);
        selected = true;
    }

    /**
     * @see Comparable#compareTo(Object)
     */
    @Override
    public int compareTo(ExtractedElement o) {
        if (o == null) {
            throw new IllegalArgumentException("Cannot compare " + toString() + " with null!");
        }
        return name.compareTo(o.getName());
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this.getClass() == obj.getClass()) { // same class
            return getFullName().equals(((ExtractedElement) obj).getFullName()); // same full name
        }
        return false;
    }

    /**
     * Asccessor for the full element name.
     * @return the full name of the element, consisting out of the package path and
     * the element name separated by an dot.
     */
    public String getFullName() {
        if ("".equals(parent)) {
            return name;
        }
        return parent + '.' + name;
    }

    /**
     * accessor for the element name.
     * @return the element name.
     */
    public String getName() {
        return name;
    }

    /**
     * accessor for the name of the elements parent.
     * @return the parent name.
     */
    public String getParentName() {
        return parent;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getFullName() == null) ? 0 : getFullName().hashCode());
        return result;
    }

    /**
     * Checks whether the element is selected.
     * @return true if selected.
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Sets whether is element is selected or not. All child elements like types or
     * subpackages will be (de)selected.
     * @param selected true to select the element, false to deselect.
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + getFullName() + ")";
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
     * Creates the name of a class or package from a full qualified name with both
     * path and name.
     * @param fullName is the full qualified name.
     * @return the name.
     */
    protected final String createName(String fullName) {
        if (fullName.contains(".")) {
            return fullName.substring(separator(fullName) + 1); // get name
        } else { // split name from parent package:
            return fullName; // already is name.
        }
    }

    /**
     * Creates the path of a class or package from a full qualified name with both
     * path and name.
     * @param fullName is the full qualified name.
     * @return the path.
     */
    protected final String createPath(String fullName) {
        if (fullName.contains(".")) {
            return fullName.substring(0, separator(fullName)); // get path
        } else { // split name from parent package:
            return ""; // has no parent.
        }
    }
}