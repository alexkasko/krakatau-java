package com.alexkasko.krakatau;


import org.apache.commons.cli.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.System.out;

/**
 * User: alexkasko
 * Date: 9/29/13
 */
public class Launcher {

    private static final String VERSION = "Krakatau  Copyright (C) 2012-13  Robert Grosse;" +
            " Java wrapper v1.0 by Alex Kasko (alexkasko.com)";
    private static final String HELP_OPTION = "help";
    private static final String VERSION_OPTION = "version";
    private static final String DECOMPILE_OPTION = "decompile";
    private static final String DISASSEMBLE_OPTION = "disassemble";
    private static final String ASSEMBLE_OPTION = "assemble";
    private static final String FILES_OPTION = "files";
    private static final String CLASSNAMES_OPTION = "classnames";
    private static final String OUTPUT_OPTION = "level";
    private static final Options OPTIONS = new Options()
            .addOption("h", HELP_OPTION, false, "show this page")
            .addOption("v", VERSION_OPTION, false, "show version")
            .addOption("s", DECOMPILE_OPTION, false, "decompile list of fully qualified class names" +
                    " from the specified classpath into source files")
            .addOption("d", DISASSEMBLE_OPTION, false, "disassemble list of class files into asm files")
            .addOption("a", ASSEMBLE_OPTION, false, "assemble list of asm files into class files")
            .addOption("f", FILES_OPTION, true, "list of files or a classpath elements, use ':' as separator")
            .addOption("n", CLASSNAMES_OPTION, true, "list of fully qualified class names, use ':' as separator")
            .addOption("o", OUTPUT_OPTION, true, "output directory");

    public static void main(String[] args) throws Exception {
        try {
            CommandLine cline = new GnuParser().parse(OPTIONS, args);
            if (cline.hasOption(VERSION_OPTION)) {
                out.println(VERSION);
            } else if (cline.hasOption(HELP_OPTION)) {
                throw new ParseException("Printing help page:");
            } else if (cline.hasOption(DECOMPILE_OPTION) &&
                    cline.hasOption(FILES_OPTION) &&
                    cline.hasOption(CLASSNAMES_OPTION) &&
                    cline.hasOption(OUTPUT_OPTION) &&
                    0 == cline.getArgs().length) {
                List<File> classpath = argToFileList(cline.getOptionValue(FILES_OPTION));
                List<String> names = argToStringList(cline.getOptionValue(CLASSNAMES_OPTION));
                File dir = new File(cline.getOptionValue(OUTPUT_OPTION));
                System.out.println("Initializing decompiler ...");
                new KrakatauLibrary().decompile(classpath, names, dir);
            } else if (cline.hasOption(DISASSEMBLE_OPTION) &&
                    cline.hasOption(FILES_OPTION) &&
                    cline.hasOption(OUTPUT_OPTION) &&
                    0 == cline.getArgs().length) {
                List<File> files = argToFileList(cline.getOptionValue(FILES_OPTION));
                File dir = new File(cline.getOptionValue(OUTPUT_OPTION));
                System.out.println("Initializing disassembled ...");
                new KrakatauLibrary().disassemble(files, dir);
            } else if (cline.hasOption(ASSEMBLE_OPTION) &&
                    cline.hasOption(FILES_OPTION) &&
                    cline.hasOption(OUTPUT_OPTION) &&
                    0 == cline.getArgs().length) {
                List<File> files = argToFileList(cline.getOptionValue(FILES_OPTION));
                File dir = new File(cline.getOptionValue(OUTPUT_OPTION));
                System.out.println("Initializing assembler ...");
                new KrakatauLibrary().assemble(files, dir);
            } else {
                throw new ParseException("Invalid arguments:");
            }
        } catch (ParseException e) {
            HelpFormatter formatter = new HelpFormatter();
            out.println(e.getMessage());
            out.println(VERSION);
            formatter.printHelp("> java -jar krakatau.jar -s -f foo.jar:bar_dir -n foo.bar.Baz1:foo.bar.Baz2 -o outdir\n" +
                    "> java -jar krakatau.jar -d -f path/to/Baz1.class:path/to/Baz2.class -o outdir\n" +
                    "> java -jar krakatau.jar -a -f path/to/Baz1.j:path/to/Baz2.j -o outdir\n"
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


//    public static void main(String[] args) {
//        interpreter.exec("import launcher");
//        interpreter.exec("launcher.launch()");
//        interpreter.exec("import decompile");
//        interpreter.exec("decompile.decompileClass(['/home/alex/java/openjdk6_b27_64/jre/lib/rt.jar', '.'], ['com/alexkasko/interview/FibonacciTest'], None, [])");

//        interpreter.exec("import disassemble");
//        interpreter.exec("disassemble.disassembleClass(disassemble.readFile, ['com/alexkasko/interview/FizzBuzzTest.class'])");

//        interpreter.exec("import assemble");
//        interpreter.exec("from Krakatau import script_util");
//        interpreter.exec("pairs = assemble.assembleClass('com/alexkasko/interview/FibonacciTest.j', True, False)");
//        interpreter.exec("for name, data in pairs:\n" +
//                "    filename = script_util.writeFile('.', name, '.class', data)\n" +
//                "    print 'Class written to', filename");
//    }
}
