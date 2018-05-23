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
import eme.model.ExtractedPackage;
import eme.model.IntermediateModel;
import org.eclipse.swt.graphics.Point;

/**
 * Selection window for disabling and enabling any {@link ExtractedElement} in a {@link IntermediateModel}.
 * @author Timur Saglam
 */
public class SelectionWindow {
    private Shell shell;

    /**
     * Opens the scope selection window for a specific {@link IntermediateModel}.
     * @param model is the specific {@link IntermediateModel}.
     * @wbp.parser.entryPoint (entry point for the window builder plug-in)
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
        shell.setMinimumSize(new Point(400, 300));
        shell.setSize(1024, 768); // TODO (MEDIUM) implement full auto scaling.
        shell.setText("Select extraction scope");
        shell.setLayout(new FillLayout(SWT.HORIZONTAL));
        // Tree viewer:
        CheckboxTreeViewer treeViewer = new CheckboxTreeViewer(shell, SWT.BORDER);
        treeViewer.setAutoExpandLevel(3);
        treeViewer.setContentProvider(new TreeContentProvider());
        treeViewer.addCheckStateListener(new CheckStateListener());
        treeViewer.setCheckStateProvider(new CheckStateProvider());
        treeViewer.setInput(new ExtractedPackage[] { model.getRoot() });
        // Tree:
        Tree tree = treeViewer.getTree();
        tree.setHeaderVisible(true);
        tree.setLinesVisible(true);
        // Tree viewer column
        TreeViewerColumn treeViewerColumn = new TreeViewerColumn(treeViewer, SWT.NONE);
        treeViewerColumn.setLabelProvider(new ColumnLabelProvider());
        TreeColumn treeColumn = treeViewerColumn.getColumn();
        treeColumn.setResizable(false);
        treeColumn.setWidth(shell.getSize().x);
        treeColumn.setText("Extracted Element");
        // Auto scaling:
        shell.addListener(SWT.Resize, new ResizeListener(shell, treeColumn));
    }
}
