package eme.ui;

import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

/**
 * Listener that automatically resizes a {@link TreeViewerColumn} to the width of a {@link Shell}.
 * @author Timur Saglam
 */
public class ResizeListener implements Listener {
    private Shell shell;
    private TreeViewerColumn column;

    /**
     * Basic constructor, sets the tracked {@link Shell} and the influenced the {@link TreeViewerColumn}.
     * @param shell is the tracked {@link Shell}.
     * @param column is the influenced {@link TreeViewerColumn}.
     */
    public ResizeListener(Shell shell, TreeViewerColumn column) {
        this.shell = shell;
        this.column = column;
    }

    @Override
    public void handleEvent(Event event) {
        int width = (int) Math.floor(shell.getSize().x / 2.0); // calculate new width from new shell size
        column.getColumn().setWidth(width); // update column width
    }
}
