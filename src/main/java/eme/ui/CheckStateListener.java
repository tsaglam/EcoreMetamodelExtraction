package eme.ui;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.ICheckStateListener;

import eme.model.ExtractedElement;

/**
 * A listener which is notified of changes to the checked state of items in check box viewers. Updates the selected
 * state of the correlating intermediate model element.
 * @author Timur Saglam
 */
public class CheckStateListener implements ICheckStateListener {

    @Override
    public void checkStateChanged(CheckStateChangedEvent event) {
        Object element = event.getElement();
        if (element instanceof ExtractedElement) { // if is intermediate model element
            ((ExtractedElement) element).setSelected(event.getChecked()); // set selected if checked and vice versa
        }
    }
}
