package eme.ui;

import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;

import eme.model.ExtractedElement;
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
        treeViewer.addCheckStateListener(new CheckStateListener(model, treeViewer));
        treeViewer.setCheckStateProvider(new CheckStateProvider());
        // Tree:
        Tree tree = treeViewer.getTree();
        tree.setHeaderVisible(true);
        tree.setLinesVisible(true);
        // Name column:
        TreeViewerColumn nameColumn = new TreeViewerColumn(treeViewer, SWT.NONE);
        nameColumn.setLabelProvider(new NameLabelProvider());
        nameColumn.getColumn().setWidth(shell.getSize().x / 2);
        nameColumn.getColumn().setText("Element Name");
        // Type column:
        TreeViewerColumn typeColumn = new TreeViewerColumn(treeViewer, SWT.NONE);
        typeColumn.setLabelProvider(new TypeLabelProvider());
        typeColumn.getColumn().setWidth(shell.getSize().x / 2);
        typeColumn.getColumn().setText("Element Type");
        // TODO (HIGH) Third column for full name? Especially to detect nested types.
        // Finish content:
        shell.addListener(SWT.Resize, new ResizeListener(shell, nameColumn));
        shell.addListener(SWT.Resize, new ResizeListener(shell, typeColumn));
        treeViewer.setInput(model); // needs to be called last
    }
}
