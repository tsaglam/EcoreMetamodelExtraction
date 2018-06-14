package eme.ui;

import org.eclipse.jface.viewers.ColumnLabelProvider;

import eme.model.ExtractedElement;

/**
 * Label provider adapter that shows the full name of the elements as column
 * text.
 * @author Timur Saglam
 */
public class FullNameLabelProvider extends ColumnLabelProvider {

    /**
     * Basic constructor, creates a type label provider.
     */
    public FullNameLabelProvider() {
        super();
    }

    @Override
    public String getText(Object element) {
        if (element instanceof ExtractedElement) { // if is intermediate model element
            return ((ExtractedElement) element).getFullName(); // user other naming scheme
        }
        return super.getText(element);
    }
}