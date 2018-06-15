package eme.ui.providers;

import org.eclipse.jface.viewers.ICheckStateProvider;

import eme.model.ExtractedElement;

/**
 * Provides checked and grayed state information about intermediate model elements in trees or tables. Makes sure that
 * any {@link ExtractedElement} in the tree view is initially checked or unchecked according to its selection status.
 * @author Timur Saglam
 */
public class CheckStateProvider implements ICheckStateProvider {

    @Override
    public boolean isChecked(Object element) {
        if (element instanceof ExtractedElement) { // if is intermediate model element
            return ((ExtractedElement) element).isSelected();  // Show as checked if is selected
        }
        return false;
    }

    @Override
    public boolean isGrayed(Object element) {
        if (element instanceof ExtractedElement) { // if is intermediate model element
            return false; // Keep it not grayeds.
        }
        return true;
    }
}