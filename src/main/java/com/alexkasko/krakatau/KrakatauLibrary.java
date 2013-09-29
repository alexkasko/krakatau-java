package com.alexkasko.krakatau;

import org.python.core.Options;
import org.python.util.PythonInterpreter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * User: alexkasko
 * Date: 9/29/13
 */
public class KrakatauLibrary {
    private final PythonInterpreter python;

    public KrakatauLibrary() {
//        Options.showJavaExceptions = true;
//        Options.includeJavaStackInExceptions = true;
        this.python = new PythonInterpreter();
    }

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
        python.exec("import decompile");
        python.exec("decompile.decompileClass(" + classpathStr + ", " + classesStr + ", '" + outDir.getPath() + "')");
    }

    public void disassemble(Collection<File> classFiles, File outDir) throws KrakatauException {
        if(null == classFiles) throw new KrakatauException("Specified 'classFiles' is null");
        if(0 == classFiles.size()) throw new KrakatauException("Specified 'classFiles' is empty");
        if(null == outDir) throw new KrakatauException("Specified 'outDir' is null");
        if(!(outDir.exists() && outDir.isDirectory())) throw new KrakatauException("Invalid output directory: [" + outDir.getAbsolutePath() + "]");
        // prepare files
        List<String> paths = new ArrayList<String>(classFiles.size());
        for (File fi : classFiles) {
            if (!(fi.exists() && fi.isFile())) throw new KrakatauException("Invalid class file: [" + fi.getAbsolutePath() + "]");
            paths.add(fi.getPath());
        }
        String pathsStr = toPythonList(paths);
        // run krakatau
        python.exec("import disassemble");
        python.exec("disassemble.disassembleClass(disassemble.readFile, " + pathsStr + ", '" + outDir.getPath() + "')");
    }

    public void assemble(Collection<File> asmFiles, File outDir) throws KrakatauException {
        if(null == asmFiles) throw new KrakatauException("Specified 'asmFiles' is null");
        if(0 == asmFiles.size()) throw new KrakatauException("Specified 'asmFiles' is empty");
        if(null == outDir) throw new KrakatauException("Specified 'outDir' is null");
        if(!(outDir.exists() && outDir.isDirectory())) throw new KrakatauException("Invalid output directory: [" + outDir.getAbsolutePath() + "]");
        for (File fi : asmFiles) {
            if (!(fi.exists() && fi.isFile())) throw new KrakatauException("Invalid asm file: [" + fi.getAbsolutePath() + "]");
        }
        // run krakatau
        python.exec("import assemble");
        python.exec("from Krakatau import script_util");
        for (File fi : asmFiles) {
            python.exec("pairs = assemble.assembleClass('" + fi.getPath() + "', True, False)");
            python.exec("for name, data in pairs:\n" +
                        "    filename = script_util.writeFile('" + outDir.getPath() + "', name, '.class', data)\n" +
                        "    print 'Class written to', filename");
        }
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
}
