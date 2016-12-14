package eme.generator.saving;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

/**
 * Utility class for creating an empty EMF project.
 * @author Timur Saglam
 */
public class EMFProjectGenerator { // TODO (MEDIUM) improve project creation.

    /**
     * Generate a new empty EMFProjectGeneratorproject.
     * @param projectName is the name of the new project.
     * @return the project as IProject object.
     */
    public static IProject createProject(String projectName) {
        IProject project = null;
        try {
            IWorkspace workspace = ResourcesPlugin.getWorkspace();
            int counter = 2;
            String name = null;
            do {
                if (name == null) {
                    name = projectName;
                } else {
                    name = projectName + counter;
                    counter++;
                }
                project = workspace.getRoot().getProject(name);
            } while (project.exists());
            IJavaProject javaProject = JavaCore.create(project);
            IProjectDescription projectDescription = ResourcesPlugin.getWorkspace().newProjectDescription(name);
            projectDescription.setLocation(null);
            project.create(projectDescription, null);
            List<IClasspathEntry> classpathEntries = new ArrayList<IClasspathEntry>();
            projectDescription.setNatureIds(new String[] { JavaCore.NATURE_ID, "org.eclipse.pde.PluginNature" });
            ICommand java = projectDescription.newCommand();
            java.setBuilderName(JavaCore.BUILDER_ID);
            ICommand manifest = projectDescription.newCommand();
            manifest.setBuilderName("org.eclipse.pde.ManifestBuilder");
            ICommand schema = projectDescription.newCommand();
            schema.setBuilderName("org.eclipse.pde.SchemaBuilder");
            ICommand oaw = projectDescription.newCommand();
            projectDescription.setBuildSpec(new ICommand[] { java, manifest, schema, oaw });
            project.open(null);
            project.setDescription(projectDescription, null);
            IFolder srcContainer = project.getFolder("src");
            if (!srcContainer.exists()) {
                srcContainer.create(false, true, null);
            }
            IClasspathEntry srcClasspathEntry = JavaCore.newSourceEntry(srcContainer.getFullPath());
            classpathEntries.add(0, srcClasspathEntry);
            classpathEntries.add(JavaCore.newContainerEntry(
                    new Path("org.eclipse.jdt.launching.JRE_CONTAINER/org.eclipse.jdt.internal.debug.ui.launcher.StandardVMType/JavaSE-1.7")));
            classpathEntries.add(JavaCore.newContainerEntry(new Path("org.eclipse.pde.core.requiredPlugins")));
            javaProject.setRawClasspath(classpathEntries.toArray(new IClasspathEntry[classpathEntries.size()]), null);
            javaProject.setOutputLocation(new Path("/" + name + "/bin"), null);
            createManifest(name, project);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return project;
    }

    /**
     * Creates file.
     */
    private static IFile createFile(String name, IContainer container, String content, String charSet) throws CoreException {
        IFile file = container.getFile(new Path(name));
        try {
            InputStream stream = new ByteArrayInputStream(content.getBytes(file.getCharset()));
            if (file.exists()) {
                file.setContents(stream, true, true, null);
            } else {
                file.create(stream, true, null);
            }
            stream.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        if (file != null && charSet != null) {
            file.setCharset(charSet, null);
        }
        return file;
    }

    /**
     * Creates manifest file.
     */
    private static void createManifest(String projectName, IProject project) throws CoreException {
        StringBuilder manifestContent = new StringBuilder("Manifest-Version: 1.0\n");
        manifestContent.append("Bundle-ManifestVersion: 2\n");
        manifestContent.append("Bundle-Name: " + projectName + "\n");
        manifestContent.append("Bundle-SymbolicName: " + projectName + "; singleton:=true\n");
        manifestContent.append("Bundle-Version: 1.0.0\n");
        manifestContent.append("Require-Bundle: ");
        manifestContent.append(" org.eclipse.emf.ecore\n");
        IFolder metaInf = project.getFolder("META-INF");
        metaInf.create(false, true, null);
        createFile("MANIFEST.MF", metaInf, manifestContent.toString(), null);
    }
}