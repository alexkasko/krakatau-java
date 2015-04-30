package com.alexkasko.krakatau.maven;

import com.alexkasko.krakatau.KrakatauLibrary;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;
import java.util.Arrays;

/**
 * Assembles Java ASM (.j) files into class files using Krakatau python library
 *
 * @goal assemble
 *
 * @author alexkasko
 * Date: 10/1/13
 */
public class KrakatauAssembleMojo extends AbstractMojo {

    /**
     * List of ASM (.j) files (or directories)
     *
     * @parameter expression="${asmFileOrDirs}"
     * @required
     */
    private File[] asmFileOrDirs;
    /**
     * Output directory
     *
     * @parameter expression="${outputDir}"
     * @required
     */
    private File outputDir;

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        KrakatauLibrary lib = new KrakatauLibrary();
        if(!outputDir.exists()) outputDir.mkdirs();
        lib.assemble(Arrays.asList(asmFileOrDirs), outputDir);
        lib.cleanup();
    }
}
