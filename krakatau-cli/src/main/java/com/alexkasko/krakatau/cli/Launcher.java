package com.alexkasko.krakatau.cli;

import com.alexkasko.krakatau.KrakatauLibrary;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.lang.System.out;

/**
 * Krakatau library command-line frontend
 *
 * @author alexkasko
 * Date: 9/29/13
 */
public class Launcher {

    private static final String VERSION = "Krakatau  Copyright (C) 2012-15  Robert Grosse;" +
            " Java wrapper v1.1 by Alex Kasko (alexkasko.com)";
    private static final String HELP_OPTION = "help";
    private static final String VERSION_OPTION = "version";
    private static final String COMPILE_OPTION = "compile";
    private static final String COMPILE_LANG_LEVEL_OPTION = "compile-lang-level";
    private static final String DECOMPILE_OPTION = "decompile";
    private static final String DISASSEMBLE_OPTION = "disassemble";
    private static final String ASSEMBLE_OPTION = "assemble";
    private static final String CLASSPATH_OPTION = "classpath";
    private static final String OUTPUT_OPTION = "output";
    private static final Options OPTIONS = new Options()
            .addOption("h", HELP_OPTION, false, "show this page")
            .addOption("v", VERSION_OPTION, false, "show version")
            .addOption("c", COMPILE_OPTION, true, "compile list of source files (or directories) using classpath specified with '-p'")
            .addOption("l", COMPILE_LANG_LEVEL_OPTION, true, "language level (3-8) to use during complation")
            .addOption("s", DECOMPILE_OPTION, true, "decompile list of fully qualified class names" +
                    " from the specified classpath into source files")
            .addOption("d", DISASSEMBLE_OPTION, true, "disassemble list of class files (or directories) into asm files")
            .addOption("a", ASSEMBLE_OPTION, true, "assemble list of asm files (or directories) into class files")
            .addOption("p", CLASSPATH_OPTION, true, "list of classpath elements (directories or .jar files) separated by ':'")
            .addOption("o", OUTPUT_OPTION, true, "output directory");

    /**
     * Entry point
     *
     * @param args arguments
     */
    public static void main(String[] args) {
        try {
            CommandLine cline = new GnuParser().parse(OPTIONS, args);
            if (cline.hasOption(VERSION_OPTION)) {
                out.println(VERSION);
            } else if (cline.hasOption(HELP_OPTION)) {
                throw new ParseException("Printing help page:");
            } else if (cline.hasOption(DECOMPILE_OPTION) &&
                    cline.hasOption(CLASSPATH_OPTION) &&
                    cline.hasOption(OUTPUT_OPTION) &&
                    0 == cline.getArgs().length) {
                List<String> names = argToStringList(cline.getOptionValue(DECOMPILE_OPTION));
                List<File> classpath = argToFileList(cline.getOptionValue(CLASSPATH_OPTION));
                File dir = new File(cline.getOptionValue(OUTPUT_OPTION));
                System.out.println("Initializing decompiler ...");
                new KrakatauLibrary().decompile(classpath, names, dir);
            } else if (cline.hasOption(DISASSEMBLE_OPTION) &&
                    cline.hasOption(OUTPUT_OPTION) &&
                    0 == cline.getArgs().length) {
                List<File> files = argToFileList(cline.getOptionValue(DISASSEMBLE_OPTION));
                File dir = new File(cline.getOptionValue(OUTPUT_OPTION));
                System.out.println("Initializing disassembler ...");
                new KrakatauLibrary().disassemble(files, dir);
            } else if (cline.hasOption(ASSEMBLE_OPTION) &&
                    cline.hasOption(OUTPUT_OPTION) &&
                    0 == cline.getArgs().length) {
                List<File> files = argToFileList(cline.getOptionValue(ASSEMBLE_OPTION));
                File dir = new File(cline.getOptionValue(OUTPUT_OPTION));
                System.out.println("Initializing assembler ...");
                new KrakatauLibrary().assemble(files, dir);
            } else if(cline.hasOption(COMPILE_OPTION) &&
                    cline.hasOption(OUTPUT_OPTION) &&
                    0 == cline.getArgs().length) {
                System.out.println("Initializing compiler ...");
                int level = extractLangLevel(cline);
                List<File> sources = argToFileList(cline.getOptionValue(COMPILE_OPTION));
                List<File> classpath = cline.hasOption(CLASSPATH_OPTION) ?
                        argToFileList(cline.getOptionValue(CLASSPATH_OPTION)) :
                        Collections.<File>emptyList();
                File dir = new File(cline.getOptionValue(OUTPUT_OPTION));
                new KrakatauLibrary().compile(sources, level, classpath, dir, new OutputStreamWriter(System.err));
                System.out.println("Compilation complete");
            } else {
                throw new ParseException("Invalid arguments:");
            }
        } catch (ParseException e) {
            HelpFormatter formatter = new HelpFormatter();
            out.println(e.getMessage());
            out.println(VERSION);
            formatter.printHelp(" > java -jar krakatau.jar -c path/to/src_dir:path/to/Baz1.class:path/to/Baz2.class" +
                    " -p foo.jar:bar_dir -o outdir\n" +
                    "> java -jar krakatau.jar -s foo.bar.Baz1:foo.bar.Baz2 -p foo.jar:bar_dir -o outdir\n" +
                    "> java -jar krakatau.jar -d path/to/class_dir:path/to/Baz1.class:path/to/Baz2.class -o outdir\n" +
                    "> java -jar krakatau.jar -a path/to/asm_dir:path/to/Baz1.j:path/to/Baz2.j -o outdir\n"
                    , OPTIONS);
        }
    }

    private static List<File> argToFileList(String arg) {
        List<String> paths = argToStringList(arg);
        List<File> files = new ArrayList<File>(paths.size());
        for(String pa : paths) {
            files.add(new File(pa));
        }
        return files;
    }

    private static List<String> argToStringList(String arg) {
        String[] parts = arg.split(":");
        return Arrays.asList(parts);
    }

    private static int extractLangLevel(CommandLine cline) {
        if (cline.hasOption(COMPILE_LANG_LEVEL_OPTION)) {
            return Integer.parseInt(cline.getOptionValue(COMPILE_LANG_LEVEL_OPTION));
        }
        return 8;
    }
}
