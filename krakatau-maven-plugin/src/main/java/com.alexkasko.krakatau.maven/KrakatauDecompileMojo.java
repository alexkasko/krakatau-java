package com.alexkasko.krakatau.maven;

import com.alexkasko.krakatau.KrakatauLibrary;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Decompile class files into Java sources using Krakatau python library
 *
 * @goal decompile
 * @phase compile
 *
 * @author alexkasko
 * Date: 10/1/13
 */
public class KrakatauDecompileMojo extends AbstractMojo {

    /**
     * List of fully-qualified class names
     *
     * @parameter expression="${classNames}"
     * @required
     */
    private String[] classNames;
    /**
     * Lists of root class directories to add to classpath
     *
     * @parameter expression="${classDirs}"
     * @required
     */
    private File[] classDirs;
    /**
     * Output directory
     *
     * @parameter expression="${outputDir}"
     * @required
     */
    private File outputDir;
    /**
     * @parameter default-value="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        KrakatauLibrary lib = new KrakatauLibrary();
        Set<Artifact> deps = project.getDependencyArtifacts();
        List<File> classpath = new ArrayList<File>(deps.size());
        for(Artifact ar : deps) {
            File fi = ar.getFile();
            if(null == fi) throw new MojoFailureException("Cannot resolve dependencies for compilation" +
                    " classpath, use 'compile' phase for this goal for proper dependency resolve");
            classpath.add(fi);
        }
        for(File fi : classDirs) {
            classpath.add(fi);
        }
        if(!outputDir.exists()) outputDir.mkdirs();
        lib.decompile(classpath, Arrays.asList(classNames), outputDir);
    }
}
