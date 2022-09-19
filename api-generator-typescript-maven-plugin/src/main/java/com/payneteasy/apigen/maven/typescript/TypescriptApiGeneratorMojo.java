package com.payneteasy.apigen.maven.typescript;

import com.payneteasy.apigen.core.typescript.CreateTypescript;
import jakarta.ws.rs.Path;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

@Mojo(name = "generate",
        defaultPhase = LifecyclePhase.COMPILE,
        requiresDependencyResolution = ResolutionScope.COMPILE,
        requiresDependencyCollection = ResolutionScope.COMPILE)

public class TypescriptApiGeneratorMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

    @Parameter(defaultValue = "/api", required = true, readonly = true)
    private String prefixSegment;
//
//    @Parameter(defaultValue = "role_name", required = true, readonly = true)
//    private String metaLoginRoleName;

    @Parameter(defaultValue = "target/api", required = true, readonly = true)
    private File targetDir;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        FileUtils.createDirectories(targetDir);

        ClassLoader classLoader = this.getClassLoader();
        Reflections reflections = new Reflections("", new SubTypesScanner(false), classLoader);
        reflections.getAllTypes().stream()
                .map(name -> loadClass(classLoader, name))
                .filter(this::hasPathAnnotation)
                .forEach(this::generateApi);

    }

    private void generateApi(Class<?> clazz) {
        getLog().info("Generating API for class " + clazz);

        File packageDir = FileUtils.createDirectories(new File(targetDir, ClassUtils.getLastPackageName(clazz)));

        CreateTypescript createTypescript = new CreateTypescript(prefixSegment);
        String           text             = createTypescript.generateTypescript(clazz);

        FileUtils.writeTextToFile(text, new File(packageDir, clazz.getSimpleName() + ".ts"));
    }

    private boolean hasPathAnnotation(Class<?> clazz) {
        return clazz.isAnnotationPresent(Path.class);
    }

    private Class<?> loadClass(ClassLoader classLoader, String name) {
        try {
            return classLoader.loadClass(name);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Cannot load class " + name, e);
        }
    }

    private ClassLoader getClassLoader() {
        List<String> runtimeClasspathElements = null;
        try {
            runtimeClasspathElements = project.getRuntimeClasspathElements();
        } catch (DependencyResolutionRequiredException e) {
            throw new IllegalStateException("Failed to resolve runtime classpath elements");
            //this.getLog().error("Failed to resolve runtime classpath elements", e);
        }

        URL[] runtimeUrls = new URL[runtimeClasspathElements.size()];
        for (int i = 0; i < runtimeClasspathElements.size(); i++) {
            String element = runtimeClasspathElements.get(i);
            try {
                runtimeUrls[i] = new File(element).toURI().toURL();
            } catch (MalformedURLException e) {
                this.getLog().error("Failed to resolve runtime classpath element", e);
            }
        }
        return new URLClassLoader(runtimeUrls,
                Thread.currentThread().getContextClassLoader());

    }

}
