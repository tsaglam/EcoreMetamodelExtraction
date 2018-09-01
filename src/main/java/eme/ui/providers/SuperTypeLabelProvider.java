package eme.ui.providers;

import java.util.LinkedList;
import java.util.List;
import java.util.StringJoiner;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

import eme.model.ExtractedClass;
import eme.model.ExtractedElement;
import eme.model.ExtractedType;
import eme.model.IntermediateModel;
import eme.model.datatypes.ExtractedDataType;

/**
 * Label provider adapter that shows the super types of the elements type as column text.
 * @author Timur Saglam
 */
public class SuperTypeLabelProvider extends GenericColumnLabelProvider<ExtractedElement> {
    private static String TOOL_TIP_INFO = "Yellow means at least one is an external type, red means at least one is not selected.";
    private final IntermediateModel model;
    private final Color errorColor;
    private final Color errorColor2;
    private final Color warningColor;

    /**
     * Basic constructor, creates a type label provider.
     * @param model is the {@link IntermediateModel} which is used a input for the tree view.
     */
    public SuperTypeLabelProvider(IntermediateModel model) {
        super(ExtractedElement.class);
        this.model = model;
        warningColor = new Color(Display.getCurrent(), 255, 255, 205);
        errorColor = new Color(Display.getCurrent(), 255, 155, 150);
        errorColor2 = new Color(Display.getCurrent(), 255, 190, 150);
    }

    @Override
    public Color getColumnBackground(ExtractedElement element) {
        if (element.isSelected()) { // only show warnings/errors for selected elements.
            if (hasSelection(false, getSuperTypes(element))) {
                return errorColor; // at least one super type is not selected
            } else if (hasExternalSupertype(element)) {
                return warningColor; // at least one super type is an external type
            }
        } else if (element instanceof ExtractedClass && hasSelection(true, getSuperInterfaces(element))) {
            return errorColor2; // class deslected but super interface is
        }
        return null; // no background color
    }

    @Override
    public String getColumnText(ExtractedElement element) {
        StringJoiner joiner = new StringJoiner(", ");
        for (ExtractedDataType superType : getSuperTypes(element)) {
            joiner.add(superType.getFullType()); // add all super types to joiner
        }
        return joiner.toString(); // return comma separated list of super types
    }

    @Override
    public String getColumnToolTip(ExtractedElement element) {
        return TOOL_TIP_INFO;
    }

    /**
     * Returns list of super types, if the element is a type which can own super types (any {@link ExtractedType}).
     */
    private List<ExtractedDataType> getSuperTypes(ExtractedElement element) {
        List<ExtractedDataType> superTypes = new LinkedList<>();
        superTypes.addAll(getSuperInterfaces(element));
        if (element instanceof ExtractedClass) { // add super classes
            ExtractedDataType superClass = ((ExtractedClass) element).getSuperClass();
            if (superClass != null) { // super class reference can be null
                superTypes.add(superClass);
            }
        }
        return superTypes;
    }

    /**
     * Returns list of super interfaces, if the element is a type which can own super interfaces (any
     * {@link ExtractedType}).
     */
    private List<ExtractedDataType> getSuperInterfaces(ExtractedElement element) {
        if (element instanceof ExtractedType) { // add super interfaces:
            return ((ExtractedType) element).getSuperInterfaces();
        }
        return new LinkedList<>();
    }

    /**
     * Checks whether at least one referenced super type is an external type.
     */
    private boolean hasExternalSupertype(ExtractedElement element) {
        for (ExtractedDataType type : getSuperTypes(element)) {
            if (model.containsExternal(type.getFullType())) {
                return true; // at least one super type is an external type
            }
        }
        return false;
    }

    /**
     * Checks whether at least one {@link ExtractedDataType} of a list has a specific selection defined by the value. This
     * means either at least one is selected or deselected depending on the value parameter.
     */
    private boolean hasSelection(boolean value, List<ExtractedDataType> types) {
        for (ExtractedDataType type : types) {
            if (model.contains(type.getFullType()) && model.getType(type.getFullType()).isSelected() == value) {
                return true; // at least one super type is not selected
            }
        }
        return false;
    }
}