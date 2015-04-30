package com.alexkasko.krakatau.maven;

import com.alexkasko.krakatau.KrakatauLibrary;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;
import java.util.Arrays;

/**
 * Disassembles class files to ASM (.j) files using Krakatau python library
 *
 * @goal disassemble
 *
 * @author alexkasko
 * Date: 10/1/13
 */
public class KrakatauDisassembleMojo extends AbstractMojo {

    /**
     * List of ASM (.j) files (or directories)
     *
     * @parameter expression="${classFileOrDirs}"
     * @required
     */
    private File[] classFileOrDirs;
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
        lib.disassemble(Arrays.asList(classFileOrDirs), outputDir);
        lib.cleanup();
    }
}
