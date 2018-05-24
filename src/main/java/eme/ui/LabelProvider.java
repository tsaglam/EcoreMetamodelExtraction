package eme.ui;

import org.eclipse.jdt.ui.ISharedImages;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Image;

import eme.model.ExtractedClass;
import eme.model.ExtractedElement;
import eme.model.ExtractedEnum;
import eme.model.ExtractedInterface;
import eme.model.ExtractedPackage;

/**
 * Label provider adapter for {@link ColumnLabelProvider} that uses the information given by the intermediate model to
 * show specific text for intermediate model elements.
 * @author Timur Saglam
 */
public class LabelProvider extends ColumnLabelProvider {

    /**
     * Basic constructor, creates a column label provider for intermediate model elements.
     */
    public LabelProvider() {
        super();
    }

    @Override
    public Image getImage(Object element) {
        if (element instanceof ExtractedPackage) { // TODO (MEDIUM) make this a little bit cleaner.
            return getEclipseImage(ISharedImages.IMG_OBJS_PACKAGE);
        } else if (element instanceof ExtractedClass) {
            return getEclipseImage(ISharedImages.IMG_OBJS_CLASS);
        } else if (element instanceof ExtractedInterface) {
            return getEclipseImage(ISharedImages.IMG_OBJS_INTERFACE);
        } else if (element instanceof ExtractedEnum) {
            return getEclipseImage(ISharedImages.IMG_OBJS_ENUM);
        }
        return super.getImage(element);
    }

    @Override
    public String getText(Object element) {
        if (element instanceof ExtractedElement) { // if is intermediate model element
            return getText((ExtractedElement) element); // user other naming scheme
        }
        return super.getText(element);
    }

    /**
     * Grants access to the Eclipse {@link JavaUI} shared images with the String contained in {@link ISharedImages}.
     */
    private Image getEclipseImage(String symbolicName) {
        return JavaUI.getSharedImages().getImage(symbolicName);
    }

    /**
     * Creates the name for the column text of a {@link ExtractedElement}. Contains special case for the default
     * package.
     */
    private String getName(ExtractedElement element) {
        if (element.getName().isEmpty() && element instanceof ExtractedPackage) {
            return "(default package)";
        }
        return element.getName();
    }

    /**
     * Provides the column text for every {@link ExtractedElement} by using its name and class. This avoids using the
     * method {@link ExtractedElement#toString()} which is not as readable in a tree view.
     */
    private String getText(ExtractedElement element) {
        return getName(element) + " [" + element.getClass().getSimpleName() + "]";
    }
}