package eme.ui;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

/**
 * Listener that automatically resizes a {@link TreeColumn} to the width of a {@link Shell}.
 * @author Timur Saglam
 */
public class ResizeListener implements Listener {
    private Shell shell;
    private Tree tree;

    /**
     * Basic constructor, sets the tracked {@link Shell} and the influenced the {@link TreeColumn}.
     * @param shell is the tracked {@link Shell}.
     * @param tree is the {@link Tree} that contains the {@link TreeColumn}.
     */
    public ResizeListener(Shell shell, Tree tree) {
        this.shell = shell;
        this.tree = tree;
    }

    @Override
    public void handleEvent(Event event) {
        int width = (int) Math.floor(shell.getSize().x / tree.getColumnCount()); // calculate new width from new shell size
        for (TreeColumn column : tree.getColumns()) { // for every column of the tree
            column.setWidth(width); // update column width
        }
    }
}
