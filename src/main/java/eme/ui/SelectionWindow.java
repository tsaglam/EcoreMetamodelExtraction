package eme.ui;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ICheckStateProvider;
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
    protected Shell shell;

    /**
     * Opens the window for a specific intermediate model.
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
     * Create contents of the window.
     */
    protected void createContents(IntermediateModel model) {
        shell = new Shell();
        shell.setSize(1024, 768);
        shell.setText("Select extraction scope");
        shell.setLayout(new FillLayout(SWT.HORIZONTAL));

        final CheckboxTreeViewer treeViewer = new CheckboxTreeViewer(shell, SWT.BORDER);
        final TreeContentProvider treeContentProvider = new TreeContentProvider();
        treeViewer.setContentProvider(treeContentProvider);
        Tree tree = treeViewer.getTree();
        tree.setHeaderVisible(true);
        tree.setLinesVisible(true);

        treeViewer.addCheckStateListener(new ICheckStateListener() {
            public void checkStateChanged(CheckStateChangedEvent event) {
                if (event.getElement() instanceof ExtractedElement) {
                    System.err.println("Selection: " + event.getChecked());
                    ((ExtractedElement) event.getElement()).setSelected(event.getChecked());
                }
            }
        });

        treeViewer.setCheckStateProvider(new ICheckStateProvider() {
            public boolean isGrayed(Object element) {
                return false;
            }

            public boolean isChecked(Object element) {
                System.err.println(element);
                if (element instanceof ExtractedElement) {
                    return ((ExtractedElement) element).isSelected();
                }
                return false;
            }
        });

        TreeViewerColumn treeViewerColumn = new TreeViewerColumn(treeViewer, SWT.NONE);
        treeViewerColumn.setLabelProvider(new ColumnLabelProvider());
        TreeColumn treeColumn = treeViewerColumn.getColumn();
        treeColumn.setWidth(1024);
        treeColumn.setText("Extracted Element");
        treeViewer.setInput(new ExtractedElement[] { model.getRoot() });
    }
}
