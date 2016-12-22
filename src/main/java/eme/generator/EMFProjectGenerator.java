package eme.generator;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
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
import org.eclipse.jdt.core.JavaModelException;

import eme.parser.JavaProjectParser;

/**
 * Utility class for creating an empty EMF project.
 * @author Timur Saglam
 */
public abstract class EMFProjectGenerator {
    private static final Logger logger = LogManager.getLogger(JavaProjectParser.class.getName());

    /**
     * Generate a new empty EMFProjectGeneratorproject with a specified name. If a project with that
     * name already exits the new project uses the same name and a number. The actual used name can
     * be received from the returned IProject.
     * @param projectName is the name of the new project.
     * @return the project as IProject object.
     */
    public static IProject createProject(String projectName) {
        IProject project = null;
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        int version = 2;
        String finalName = projectName;
        project = workspace.getRoot().getProject(projectName);
        while (project.exists()) { // to avoid duplicates:
            finalName = projectName + version;
            version++;
            project = workspace.getRoot().getProject(finalName);
        }
        IJavaProject javaProject = JavaCore.create(project);
        IProjectDescription projectDescription = ResourcesPlugin.getWorkspace().newProjectDescription(finalName);
        projectDescription.setLocation(null);
        try {
            project.create(projectDescription, null);
        } catch (CoreException exception) {
            logger.error("Error while creating project resource in workspace", exception);
        }
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
        IFolder srcContainer = null;
        try {
            project.open(null);
            project.setDescription(projectDescription, null);
            srcContainer = project.getFolder("src");
            if (!srcContainer.exists()) {
                srcContainer.create(false, true, null);
            }
        } catch (CoreException exception) {
            logger.error("Error while building project description & source folder", exception);
        }
        IClasspathEntry srcClasspathEntry = JavaCore.newSourceEntry(srcContainer.getFullPath());
        classpathEntries.add(0, srcClasspathEntry);
        classpathEntries.add(JavaCore.newContainerEntry(
                new Path("org.eclipse.jdt.launching.JRE_CONTAINER/org.eclipse.jdt.internal.debug.ui.launcher.StandardVMType/JavaSE-1.7")));
        classpathEntries.add(JavaCore.newContainerEntry(new Path("org.eclipse.pde.core.requiredPlugins")));
        try {
            javaProject.setRawClasspath(classpathEntries.toArray(new IClasspathEntry[classpathEntries.size()]), null);
            javaProject.setOutputLocation(new Path("/" + finalName + "/bin"), null);
        } catch (JavaModelException exception) {
            logger.error("Error while building classpath & output location", exception);
        }
        createManifest(finalName, project);
        return project;
    }

    private static IFile createFile(String name, IContainer container, String content, String charSet) throws CoreException, IOException {
        IFile file = container.getFile(new Path(name));
        InputStream stream = new ByteArrayInputStream(content.getBytes(file.getCharset()));
        if (file.exists()) {
            file.setContents(stream, true, true, null);
        } else {
            file.create(stream, true, null);
        }
        stream.close();
        if (file != null && charSet != null) {
            file.setCharset(charSet, null);
        }
        return file;
    }

    private static void createManifest(String projectName, IProject project) {
        StringBuilder manifestContent = new StringBuilder("Manifest-Version: 1.0\n");
        manifestContent.append("Bundle-ManifestVersion: 2\n");
        manifestContent.append("Bundle-Name: " + projectName + "\n");
        manifestContent.append("Bundle-SymbolicName: " + projectName + "; singleton:=true\n");
        manifestContent.append("Bundle-Version: 1.0.0\n");
        manifestContent.append("Require-Bundle: ");
        manifestContent.append(" org.eclipse.emf.ecore\n");
        IFolder metaInf = project.getFolder("META-INF");
        try {
            metaInf.create(false, true, null);
            createFile("MANIFEST.MF", metaInf, manifestContent.toString(), null);
        } catch (Exception exception) {
            logger.error("Error while creating the manifest file", exception);
        }
    }
}