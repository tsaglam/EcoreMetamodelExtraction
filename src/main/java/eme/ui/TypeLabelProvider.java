package eme.ui;

import org.eclipse.jface.viewers.ColumnLabelProvider;

/**
 * Label provider adapter that shows the simple name of the elements type as column text.
 * @author Timur Saglam
 */
public class TypeLabelProvider extends ColumnLabelProvider {

    /**
     * Basic constructor, creates a type label provider.
     */
    public TypeLabelProvider() {
        super();
    }

    @Override
    public String getText(Object element) {
        return element.getClass().getSimpleName();
    }
}