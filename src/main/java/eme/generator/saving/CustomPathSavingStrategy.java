package eme.generator.saving;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * Saving strategy that lets the user choose a custom path.
 * @author Timur Saglam
 */
public class CustomPathSavingStrategy extends AbstractSavingStrategy {
    private String name;
    private String path;

    /**
     * Basic constructor. Takes the name of the project.
     * @param projectName is the name of the project where the metamodel was extracted.
     */
    public CustomPathSavingStrategy(String projectName) {
        super(projectName, false); // don't refresh folder
    }

    /*
     * @see eme.generator.saving.AbstractSavingStrategy#fileName()
     */
    @Override
    protected String fileName() {
        return name;
    }

    /*
     * @see eme.generator.saving.AbstractSavingStrategy#filePath()
     */
    @Override
    protected String filePath() {
        return path;
    }

    /**
     * Opens a saving dialog and sets the path and the name.
     */
    @Override
    protected void beforeSaving() {
        Display display = Display.getCurrent();
        Shell shell = new Shell(display);
        shell.open();
        String result = null;
        while (result == null) {
            FileDialog dialog = new FileDialog(shell, SWT.SAVE);
            dialog.setFilterNames(new String[] { "Ecore File" });
            dialog.setFilterExtensions(new String[] { "*.ecore" });
            dialog.setFileName(projectName + ".ecore");
            result = dialog.open(); // calculate result:
        }
        int index = result.lastIndexOf('/') + 1;
        path = result.substring(0, index);
        name = result.substring(index, result.lastIndexOf('.'));
        shell.close();
    }
}