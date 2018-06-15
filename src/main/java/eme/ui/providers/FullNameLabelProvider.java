package eme.ui.providers;

import eme.model.ExtractedElement;

/**
 * Label provider adapter that shows the full name of the elements as column text.
 * @author Timur Saglam
 */
public class FullNameLabelProvider extends GenericColumnLabelProvider<ExtractedElement> {

    /**
     * Basic constructor, creates a type label provider.
     */
    public FullNameLabelProvider() {
        super(ExtractedElement.class);
    }

    @Override
    public String getColumnText(ExtractedElement element) {
        return element.getFullName();
    }

    @Override
    public String getColumnToolTip(ExtractedElement element) {
        return  "full name: " + getColumnText(element);
    }
}