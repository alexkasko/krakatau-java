package com.alexkasko.krakatau;

import org.apache.commons.io.output.NullOutputStream;
import org.eclipse.jdt.internal.compiler.apt.util.EclipseFileManager;
import org.eclipse.jdt.internal.compiler.tool.EclipseCompiler;
import org.eclipse.jdt.internal.compiler.tool.EclipseFileObject;
import org.python.util.PythonInterpreter;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import java.io.File;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.*;

import static org.apache.commons.io.FileUtils.listFiles;

/**
 * Frontend library for java classfiles operations: disassembling, assembling, decompilation and compilation.
 * Uses eclipse compiler for compilation and krakatau python library (through jython) for other operations
 *
 * @author alexkasko
 * Date: 9/29/13
 */
public class KrakatauLibrary {
    private static final Charset UTF8 = Charset.forName("UTF-8");

    private final PythonInterpreter python;

    /**
     * Constructor
     */
    public KrakatauLibrary() {
//        http://bugs.jython.org/issue1435
//        Options.showJavaExceptions = true;
//        Options.includeJavaStackInExceptions = true;
        this.python = new PythonInterpreter();
    }

    /**
     * Calls cleanup on interpreter instance
     */
    public void cleanup() {
        if (null != python) {
            python.cleanup();
        }
    }

    /**
     * Decompiles list of fully qualified classes from specified classpath
     *
     * @param classpathFiles list of classpath entries (.jar files or directories)
     * @param fqClassNames list of fully qualifies class names
     * @param outDir output directory
     * @throws KrakatauException
     */
    public void decompile(Collection<File> classpathFiles, Collection<String> fqClassNames, File outDir) throws KrakatauException {
        if(null == classpathFiles) throw new KrakatauException("Specified 'classpathFiles' is null");
        if(null == fqClassNames) throw new KrakatauException("Specified 'fqClassNames' is null");
        if(0 == fqClassNames.size()) throw new KrakatauException("Specified 'fqClassNames' is empty");
        if(null == outDir) throw new KrakatauException("Specified 'outDir' is null");
        if(!(outDir.exists() && outDir.isDirectory())) throw new KrakatauException("Invalid output directory: [" + outDir.getAbsolutePath() + "]");
        // find rt.jar
        String jrePath = System.getProperty("java.home");
        File jreDir = new File(jrePath);
        if(!(jreDir.exists() && jreDir.isDirectory())) throw new KrakatauException(
                "Invalid JRE dir: [" + jreDir.getAbsolutePath() +"] obtained through 'java.home' property");
        File rtJar = new File(jreDir, "lib/rt.jar");
        if(!(rtJar.exists() && rtJar.isFile())) throw new KrakatauException(
                "Cannot access 'rt.jar' on path: [" + rtJar.getAbsolutePath() + "]");
        // prepare classpath
        List<String> classpath = new ArrayList<String>(classpathFiles.size() + 1);
        classpath.add(rtJar.getPath());
        for(File fi : classpathFiles) {
            classpath.add(fi.getPath());
        }
        String classpathStr = toPythonList(classpath);
        // prepare files
        List<String> classes = new ArrayList<String>(fqClassNames.size());
        for(String cl : fqClassNames) {
            String stripped = cl.endsWith(".class") ? cl.substring(0, cl.length() - 6) : cl;
            String name = stripped.replace(".", "/");
            classes.add(name);
        }
        String classesStr = toPythonList(classes);
        // run krakatau
        try {
            python.exec("import decompile");
            python.exec("decompile.decompileClass(" + classpathStr + ", " + classesStr + ", '" + outDir.getPath() + "')");
        } catch (Exception e) {
            throw new KrakatauException("Decompile error", e);
        }
    }

    /**
     * Disassembles list of class files (or directories) into asm (.j) files
     *
     * @param classSources list of class files (or directories)
     * @param outDir output directory
     * @throws KrakatauException
     */
    public void disassemble(Collection<File> classSources, File outDir) throws KrakatauException {
        if(null == classSources) throw new KrakatauException("Specified 'classSources' is null");
        if(0 == classSources.size()) throw new KrakatauException("Specified 'classSources' is empty");
        if(null == outDir) throw new KrakatauException("Specified 'outDir' is null");
        if(!(outDir.exists() && outDir.isDirectory())) throw new KrakatauException("Invalid output directory: [" + outDir.getAbsolutePath() + "]");
        // prepare files
        List<File> classFiles = expandDirs(classSources, "class");
        List<String> paths = new ArrayList<String>(classFiles.size());
        for (File fi : classFiles) {
            paths.add(fi.getPath());
        }
        String pathsStr = toPythonList(paths);
        // run krakatau
        try {
            python.exec("import disassemble");
            python.exec("disassemble.disassembleClass(disassemble.readFile, " + pathsStr + ", '" + outDir.getPath() + "')");
        } catch (Exception e) {
            throw new KrakatauException("Disassemble error", e);
        }
    }

    /**
     * Assembled list of asm (.j) files (or directories) into class files
     *
     * @param asmSources list of asm files (or directories)
     * @param outDir output directory
     * @throws KrakatauException
     */
    public void assemble(Collection<File> asmSources, File outDir) throws KrakatauException {
        if(null == asmSources) throw new KrakatauException("Specified 'asmSources' is null");
        if(0 == asmSources.size()) throw new KrakatauException("Specified 'asmSources' is empty");
        if(null == outDir) throw new KrakatauException("Specified 'outDir' is null");
        if(!(outDir.exists() && outDir.isDirectory())) throw new KrakatauException("Invalid output directory: [" + outDir.getAbsolutePath() + "]");
        List<File> asmFiles = expandDirs(asmSources, "j");
        // run krakatau
        try {
            python.exec("import assemble");
            python.exec("from Krakatau import script_util");
            for (File fi : asmFiles) {
                python.exec("pairs = assemble.assembleClass('" + fi.getPath() + "', True, False)");
                python.exec("out = script_util.makeWriter('" + outDir.getPath() + "', '.class')");
                python.exec("for name, data in pairs:\n" +
                        "    filename = out.write(name, data)\n" +
                        "    print 'Class written to', filename");
            }
        } catch (Exception e) {
            throw new KrakatauException("Assemble error", e);
        }
    }

    /**
     * Compiles list of source files (or directories)
     *
     * @param sources list of source files (or directories)
     * @param langLevel java language and bytecode compliance level (3 - 8)
     * @param classpathFiles list of classpath entries (.jar files or directories)
     * @param outDir output directory
     * @param errorWriter error writer
     * @throws KrakatauException
     */
    public void compile(List<File> sources, int langLevel, Collection<File> classpathFiles, File outDir, Writer errorWriter) throws KrakatauException {
        if(null == sources) throw new KrakatauException("Specified 'sources' is null");
        if(langLevel < 3 || langLevel > 8) throw new KrakatauException("Specified 'langLevel': [" + langLevel + "]" +
                " not supported, supported levels are from [3] to [7]");
        if(0 == sources.size()) throw new KrakatauException("Specified 'sources' is empty");
        if(null == classpathFiles) throw new KrakatauException("Specified 'classpathFiles' is null");
        if(null == outDir) throw new KrakatauException("Specified 'outDir' is null");
        if(!(outDir.exists() && outDir.isDirectory())) throw new KrakatauException("Invalid output directory: [" + outDir.getAbsolutePath() + "]");
        // options
        List<String> options = new ArrayList<String>();
        // debug
        options.add("-g");
        options.add("-1." + langLevel);
        // classpath
        if (classpathFiles.size() > 0) {
            boolean first = true;
            StringBuilder cpBuilder = new StringBuilder();
            for (File fi : classpathFiles) {
                if (first) first = false;
                else cpBuilder.append(":");
                cpBuilder.append(fi.getPath());
            }
            options.add("-classpath");
            options.add(cpBuilder.toString());
        }
        // out
        options.add("-d");
        options.add(outDir.getPath());
        // sources
        List<File> srcFiles = expandDirs(sources, "java");
        List<EclipseFileObject> compilationUnits = new ArrayList<EclipseFileObject>(srcFiles.size());
        for(File fi : srcFiles) {
            compilationUnits.add(new EclipseFileObject(null, fi.toURI(), JavaFileObject.Kind.SOURCE, UTF8));
        }
        // run compiler
        try {
            JavaCompiler compiler = new EclipseCompiler();
            JavaCompiler.CompilationTask compile = compiler.getTask(errorWriter, new EclipseFileManager(Locale.ENGLISH, UTF8),
                    null, options, null, compilationUnits);
            compile.call();
        } catch (Exception e) {
            throw new KrakatauException("Compile error", e);
        }
    }

    /**
     * 1.0 compatibility method, defaults langLevel to 8
     *
     * @param sources list of source files (or directories)
     * @param classpathFiles list of classpath entries (.jar files or directories)
     * @param outDir output directory
     * @param errorWriter error writer
     * @throws KrakatauException
     */
    public void compile(List<File> sources, Collection<File> classpathFiles, File outDir, Writer errorWriter) throws KrakatauException {
        compile(sources, 8, classpathFiles, outDir, errorWriter);
    }

    private String toPythonList(Collection<String> col) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        boolean first = true;
        for(String st : col) {
            if (first) first = false;
            else sb.append(", ");
            sb.append("'");
            sb.append(st);
            sb.append("'");
        }
        sb.append("]");
        return sb.toString();
    }

    private List<File> expandDirs(Collection<File> filesOrDirs, String extension) {
        List<File> files = new ArrayList<File>();
        for (File src : filesOrDirs) {
            if (!src.exists()) throw new KrakatauException("Invalid file or directory: [" + src.getAbsolutePath() + "]");
            if (src.isFile()) {
                files.add(src);
            } else {
                for (File fi : listFiles(src, new String[]{extension}, true)) {
                    files.add(fi);
                }
            }
        }
        return files;
    }
}
