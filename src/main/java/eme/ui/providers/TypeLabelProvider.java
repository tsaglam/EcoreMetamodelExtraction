package eme.ui.providers;

import eme.model.ExtractedElement;

/**
 * Label provider adapter that shows the simple name of the elements type as column text.
 * @author Timur Saglam
 */
public class TypeLabelProvider extends GenericColumnLabelProvider<ExtractedElement> {

    /**
     * Basic constructor, creates a type label provider.
     */
    public TypeLabelProvider() {
        super(ExtractedElement.class);
    }

    @Override
    public String getColumnText(ExtractedElement element) {
        return element.getClass().getSimpleName();
    }

    @Override
    public String getColumnToolTip(ExtractedElement element) {
        return "element type: " + getColumnText(element);
    }
}