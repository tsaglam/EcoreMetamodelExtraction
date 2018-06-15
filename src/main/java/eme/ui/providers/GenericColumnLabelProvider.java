package eme.ui.providers;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * This class is a generic version of the {@link ColumnLabelProvider}. Because the {@link ColumnLabelProvider} class
 * uses objects instead of generics, this adapter class has to be used to implement a column label provider with a
 * generic type parameter and generic typed methods.
 * @author Timur Saglam
 * @param T is the type of the contained elements.
 */
public abstract class GenericColumnLabelProvider<T> extends ColumnLabelProvider {
    private final Class<? extends T> clazz;

    /**
     * Basic constructor, creates a generic {@link ColumnLabelProvider}.
     * @param clazz is the {@link Class} of the generic type parameter.
     */
    public GenericColumnLabelProvider(Class<? extends T> clazz) {
        super();
        this.clazz = clazz;
    }

    /**
     * @see ColumnLabelProvider#getImage(Object)
     */
    public Image getColumnImage(T element) {
        return super.getImage(element); // default implementation, delegates to super class.
    }

    /**
     * @see ColumnLabelProvider#getText(Object)
     */
    public String getColumnText(T element) {
        return super.getText(element); // default implementation, delegates to super class.
    }

    /**
     * @see ColumnLabelProvider#getToolTipText(Object)
     */
    public String getColumnToolTip(T element) {
        return super.getToolTipText(element); // default implementation, delegates to super class.
    }

    @Override
    public final Image getImage(Object element) {
        T casted = cast(element); // call typed method if cast was successful:
        return casted == null ? super.getImage(element) : getColumnImage(casted);
    }

    @Override
    public final String getText(Object element) {
        T casted = cast(element); // call typed method if cast was successful:
        return casted == null ? super.getText(element) : getColumnText(casted);
    }

    @Override
    public final String getToolTipText(Object element) {
        T casted = cast(element); // call typed method if cast was successful:
        return casted == null ? super.getToolTipText(element) : getColumnToolTip(casted);
    }

    /**
     * Tries to cast a object to a instance of {@link T}. Returns null if the cast fails.
     */
    private T cast(Object object) {
        try {
            return clazz.cast(object);
        } catch (ClassCastException exception) {
            return null; // cannot cast, column will contain nothing.
        }
    }
}