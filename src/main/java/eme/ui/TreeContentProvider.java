package eme.ui;

import java.util.ArrayList;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;

import eme.model.ExtractedElement;
import eme.model.ExtractedPackage;

/**
 * Intermediate model tree content provider. Provides tree view with content from an intermediate model.
 * @author Timur Saglam
 */
public class TreeContentProvider implements ITreeContentProvider {

    public boolean hasChildren(Object element) {
        if (element instanceof ExtractedPackage) {
            return !((ExtractedPackage) element).isEmpty();
        }
        return false;
    }

    public Object getParent(Object element) {
        return null;
    }

    public Object[] getElements(Object inputElement) {
        return ArrayContentProvider.getInstance().getElements(inputElement);
    }

    public Object[] getChildren(Object parentElement) {
        if (parentElement instanceof ExtractedPackage) {
            ExtractedPackage extractedPackage = (ExtractedPackage) parentElement;
            ArrayList<ExtractedElement> list = new ArrayList<ExtractedElement>();
            list.addAll(extractedPackage.getSubpackages());
            list.addAll(extractedPackage.getTypes());
            return list.toArray();
        }
        return null;
    }
}