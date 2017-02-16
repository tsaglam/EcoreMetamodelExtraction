package eme.generator.saving;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * Saving strategy that lets the user choose a custom path to save the ecore file.
 * @author Timur Saglam
 */
public class CustomPathSaving extends AbstractSavingStrategy {
    private String name;
    private String path;

    /**
     * Basic constructor.
     */
    public CustomPathSaving() {
        super(false); // don't refresh folder
    }

    /**
     * Opens a saving dialog and sets the path and the name.
     */
    @Override
    protected void beforeSaving(String projectName) {
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
        int index = result.lastIndexOf(SLASH) + 1; 
        path = result.substring(0, index);
        name = result.substring(index, result.lastIndexOf('.'));
        shell.close();
    }

    /*
     * @see eme.generator.saving.AbstractSavingStrategy#fileName()
     */
    @Override
    protected String getFileName() {
        return name;
    }

    /*
     * @see eme.generator.saving.AbstractSavingStrategy#filePath()
     */
    @Override
    protected String getFilePath() {
        return path;
    }
}