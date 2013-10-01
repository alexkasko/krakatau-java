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
 * Compiles Java source files (or directories) using eclipse compiler.
 * This plugin was designed to be uses with other krakatau tools with no intentions
 * to replace maven-compiler-plugin
 *
 * @goal compile
 * @phase compile
 *
 * @author alexkasko
 * Date: 10/1/13
 */
public class KrakatauCompilerMojo extends AbstractMojo {

    /**
     * List of source files (or directories)
     *
     * @parameter expression="${sourceFileOrDirs}"
     * @required
     */
    private File[] sourceFileOrDirs;
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
                    " classpath, use 'compile' phase (or later) for this goal for proper dependency resolve");
            classpath.add(fi);
        }
        if(!outputDir.exists()) outputDir.mkdirs();
        lib.compile(Arrays.asList(sourceFileOrDirs), classpath, outputDir, new OutputStreamWriter(System.err));
    }
}
