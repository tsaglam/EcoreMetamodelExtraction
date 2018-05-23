package eme.ui;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TreeColumn;

/**
 * Listener that automatically resizes a {@link TreeColumn} to the width of a {@link Shell}.
 * @author Timur Saglam
 */
public class ResizeListener implements Listener {
    private Shell shell;
    private TreeColumn treeColumn;

    /**
     * Basic constructor, sets the tracked {@link Shell} and the influenced the {@link TreeColumn}.
     * @param shell is the tracked {@link Shell}.
     * @param treeColumn is the influenced {@link TreeColumn}.
     */
    public ResizeListener(Shell shell, TreeColumn treeColumn) {
        this.shell = shell;
        this.treeColumn = treeColumn;
    }

    @Override
    public void handleEvent(Event event) {
        treeColumn.setWidth(shell.getSize().x); // column width set to shell width
    }
}
