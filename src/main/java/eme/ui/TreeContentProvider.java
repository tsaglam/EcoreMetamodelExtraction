package eme.ui;

import java.util.ArrayList;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;

import eme.model.ExtractedElement;
import eme.model.ExtractedPackage;
import eme.model.IntermediateModel;

/**
 * Intermediate model tree content provider. Provides tree view with content from an intermediate model.
 * @author Timur Saglam
 */
public class TreeContentProvider implements ITreeContentProvider {

    @Override
    public boolean hasChildren(Object element) {
        if (element instanceof ExtractedPackage) {
            return hasChildren((ExtractedPackage) element);
        }
        return false;
    }

    @Override
    public Object getParent(Object element) {
        return null; // parent cannot be computed.
    }

    @Override
    public Object[] getElements(Object inputElement) {
        if (inputElement instanceof IntermediateModel) {
            return new ExtractedPackage[] { ((IntermediateModel) inputElement).getRoot() };
        }
        return ArrayContentProvider.getInstance().getElements(inputElement);
    }

    @Override
    public Object[] getChildren(Object parentElement) {
        if (parentElement instanceof ExtractedPackage) {
            return getChildren((ExtractedPackage) parentElement);
        }
        return null;
    }

    /**
     * Checks whether an {@link ExtractedPackage} is not empty.
     * @param extractedPackage is the {@link ExtractedPackage}.
     * @return true if the {@link ExtractedPackage} contains types or subpackages.
     */
    private boolean hasChildren(ExtractedPackage extractedPackage) {
        return !extractedPackage.isEmpty();
    }

    /**
     * Returns all subpackages and type of a {@link ExtractedPackage}.
     * @param extractedPackage is the {@link ExtractedPackage}
     * @return every subpackage and contained types.
     */
    private Object[] getChildren(ExtractedPackage extractedPackage) {
        ArrayList<ExtractedElement> children = new ArrayList<ExtractedElement>();
        children.addAll(extractedPackage.getSubpackages());
        children.addAll(extractedPackage.getTypes());
        return children.toArray();
    }

}