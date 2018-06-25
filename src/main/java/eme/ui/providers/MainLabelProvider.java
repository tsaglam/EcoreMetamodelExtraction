package eme.ui.providers;

import org.eclipse.jdt.ui.ISharedImages;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.swt.graphics.Image;

import eme.model.ExtractedClass;
import eme.model.ExtractedElement;
import eme.model.ExtractedEnum;
import eme.model.ExtractedInterface;
import eme.model.ExtractedPackage;

/**
 * Label provider that uses the information given by the intermediate model to show specific text for intermediate model
 * elements. It shows the simple name of the element as column text and a correlating icon.
 * @author Timur Saglam
 */
public class MainLabelProvider extends GenericColumnLabelProvider<ExtractedElement> {

    /**
     * Basic constructor, creates a column label provider for intermediate model elements.
     */
    public MainLabelProvider() {
        super(ExtractedElement.class);
    }

    @Override
    public Image getColumnImage(ExtractedElement element) {
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
    public String getColumnText(ExtractedElement element) {
        if (element.getName().isEmpty() && element instanceof ExtractedPackage) {
            return "(default package)";
        }
        return element.getName();
    }

    @Override
    public String getColumnToolTip(ExtractedElement element) {
        return "full name: " + element.getFullName();
    }

    /**
     * Grants access to the Eclipse {@link JavaUI} shared images with the String contained in {@link ISharedImages}.
     */
    private Image getEclipseImage(String symbolicName) {
        return JavaUI.getSharedImages().getImage(symbolicName);
    }
}