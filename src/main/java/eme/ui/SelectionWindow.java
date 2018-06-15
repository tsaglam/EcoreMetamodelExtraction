package eme.ui;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;

import eme.model.ExtractedElement;
import eme.model.IntermediateModel;
import eme.ui.providers.CheckStateProvider;
import eme.ui.providers.FullNameLabelProvider;
import eme.ui.providers.MainLabelProvider;
import eme.ui.providers.TreeContentProvider;
import eme.ui.providers.TypeLabelProvider;

/**
 * Selection window for disabling and enabling any {@link ExtractedElement} in a {@link IntermediateModel}.
 * @author Timur Saglam
 */
public class SelectionWindow {
    private static final int MIN_HEIGHT = 300;
    private static final int MIN_WIDTH = 400;
    private static final int HEIGHT = 768;
    private static final int WIDTH = 1024;
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
     * Creates a new {@link TreeViewerColumn} and adds it to the {@link TreeViewer}.
     * @param treeViewer is the {@link TreeViewer}.
     * @param provider is the content provider for the {@link TreeViewerColumn}.
     * @param title is the title of the {@link TreeViewerColumn}.
     */
    private void createColumn(CheckboxTreeViewer treeViewer, CellLabelProvider provider, String title) {
        TreeViewerColumn column = new TreeViewerColumn(treeViewer, SWT.NONE);
        column.setLabelProvider(provider);
        column.getColumn().setText(title);
        shell.addListener(SWT.Resize, new ResizeListener(shell, treeViewer, column));
    }

    /**
     * Create contents of the window by using the model elements of the {@link IntermediateModel}.
     * @param model is the {@link IntermediateModel}.
     */
    protected void createContents(IntermediateModel model) {
        // Shell:
        shell = new Shell();
        shell.setMinimumSize(new Point(MIN_WIDTH, MIN_HEIGHT));
        shell.setSize(WIDTH, HEIGHT); // TODO (MEDIUM) implement full auto scaling.
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
        // Columns:
        createColumn(treeViewer, new MainLabelProvider(), "ElementName");
        createColumn(treeViewer, new FullNameLabelProvider(), "Full Name");
        createColumn(treeViewer, new TypeLabelProvider(), "Element Type");
        // Finish content:
        treeViewer.setInput(model); // needs to be called last
    }
}