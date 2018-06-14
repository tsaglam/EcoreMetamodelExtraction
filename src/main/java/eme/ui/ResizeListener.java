package eme.ui;

import org.eclipse.jface.viewers.TreeViewer;
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
    private TreeViewer viewer;
    private TreeViewerColumn column;

    /**
     * Basic constructor, sets the tracked {@link Shell} and the influenced the {@link TreeViewerColumn}.
     * @param shell is the tracked {@link Shell}.
     * @param viewer is the {@link TreeViewer} that contains the {@link TreeViewerColumn}.
     * @param column is the influenced {@link TreeViewerColumn}.
     */
    public ResizeListener(Shell shell, TreeViewer viewer, TreeViewerColumn column) {
        this.shell = shell;
        this.viewer = viewer;
        this.column = column;
    }

    @Override
    public void handleEvent(Event event) {
        int columns = viewer.getTree().getColumnCount();
        int width = (int) Math.floor(shell.getSize().x / columns); // calculate new width from new shell size
        column.getColumn().setWidth(width); // update column width
    }
}
