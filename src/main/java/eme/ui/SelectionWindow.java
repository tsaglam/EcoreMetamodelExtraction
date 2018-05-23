package eme.ui;

import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

import eme.model.ExtractedElement;
import eme.model.IntermediateModel;

/**
 * Selection window for disabling and enabling any {@link ExtractedElement} in a {@link IntermediateModel}.
 * @author Timur Saglam
 */
public class SelectionWindow {
    private Shell shell;

    /**
     * Opens the scope selection window for a specific {@link IntermediateModel}.
     * @param model is the specific {@link IntermediateModel}.
     */
    public void open(IntermediateModel model) {
        Display display = Display.getDefault();
        createContents(model);
        shell.open();
        shell.layout();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
    }

    /**
     * Create contents of the window by using the model elements of the {@link IntermediateModel}.
     * @param model is the {@link IntermediateModel}.
     */
    protected void createContents(IntermediateModel model) {
        // Shell:
        shell = new Shell();
        shell.setSize(1024, 768);
        shell.setText("Select extraction scope");
        shell.setLayout(new FillLayout(SWT.HORIZONTAL));
        // Tree viewer:
        CheckboxTreeViewer treeViewer = new CheckboxTreeViewer(shell, SWT.BORDER);
        treeViewer.setContentProvider(new TreeContentProvider());
        treeViewer.addCheckStateListener(new CheckStateListener());
        treeViewer.setCheckStateProvider(new CheckStateProvider());
        treeViewer.setInput(new ExtractedElement[] { model.getRoot() });
        // Tree:
        Tree tree = treeViewer.getTree();
        tree.setHeaderVisible(true);
        tree.setLinesVisible(true);
        // Tree viewer column
        TreeViewerColumn treeViewerColumn = new TreeViewerColumn(treeViewer, SWT.NONE);
        treeViewerColumn.setLabelProvider(new ColumnLabelProvider());
        TreeColumn treeColumn = treeViewerColumn.getColumn();
        treeColumn.setWidth(1024);
        treeColumn.setText("Extracted Element");
    }
}
