Java disassembler, assembler and decompiler
===========================================

This project contains tools from [Krakatau](https://github.com/Storyyeller/Krakatau) python library
embedded into java library using [Jython](http://www.jython.org/).

Command line tool and maven plugin were built on top of this library.

Krakatau java library
---------------------

This library contains `jython-standalone` library merged with Krakatau python source code using [maven shade](http://maven.apache.org/plugins/maven-shade-plugin/) plugin.
It allows to use all Krakatau operations (disassembler, assembler and decompiler) as usual Java API without CPython.
It also contains standalone java compiler ([eclipse ecj](http://www.eclipse.org/jdt/core/)) that allows to compile
Java sources with the API similar to other operations.

Javadocs are available [here](http://alexkasko.com/krakatau-java/javadocs/).

Library dependency (from maven central):

    <dependency>
        <groupId>com.alexkasko.krakatau</groupId>
        <artifactId>krakatau-lib</artifactId>
        <version>1.1</version>
        <classifier>standalone</classifier>
        <exclusions>
            <exclusion>
                <groupId>org.python</groupId>
                <artifactId>jython-standalone</artifactId>
                </exclusion>
            <exclusion>
                <groupId>org.eclipse.jdt.core.compiler</groupId>
                <artifactId>ecj</artifactId>
            </exclusion>
            <exclusion>
                <groupId>commons-io</groupId>
                <artifactId>commons-io</artifactId>
            </exclusion>
        </exclusions>
    </dependency>

Operations:

 - **disassemble**: converts Java class file into human-readable text representation of Java byte-code (`.j` asm files)
 - **assemble**: converts asm (`.j`) files into Java class files
 - **decompile**: converts Java class files into Java source files
 - **compile**: usual compilation, added to library for completeness

API usage example:

    List<String> classes = Arrays.asList("foo.bar.Baz1", "foo.bar.Baz2")
    List<File> classpath = ...
    File outputDir = new File("out");
    KrakatauLibrary krakatau = new KrakatauLibrary().
    krakatau.decompile(classpath, classes, outputDir);

Command line tool
-----------------

Command line tool was created mainly for testing purposes, Jython slow startup make it impractical comparing to
original Krakatau.

Download [krakatau.jar-1.0](https://bitbucket.org/alexkasko/share/downloads/krakatau-1.0.jar) ([sha256sum](http://alexkasko.com/krakatau-java/checksums/krakatau-1.0.jar.sha256)).

Maven plugin
------------

This maven plugin is a kind of poor-man's IDE plugin for disassemble and decompilation of Java classes.
Plugin may be configured in pom file to be run from command line (or IDE interface) as follows
(see [pom example](https://github.com/alexkasko/krakatau-java/blob/master/krakatau-maven-plugin-test/pom.xml#L53)
and maven-generated [plugin site](http://alexkasko.com/krakatau-java/site/)):

    mvn krakatau:assemble
    mvn krakatau:disassemble
    mvn krakatau:decompile
    mvn krakatau:compile

Also decompile operation in Krakatau requires Java classpath that may be cumbersome to specify from CLI,
but very easy in Maven - project dependencies are used as a classpath.

License information
-------------------

This project is released under the [GNU Public License 3.0](http://opensource.org/licenses/gpl-3.0.html)
(it's required by original Krakatau license).

Changelog
---------

**1.1** (2015-04-30)

 * Krakatau, Jython and ECJ updated
 * language level option added to `compile` task
 * Jython cleanup fix when running from maven

**1.0.1** (2013-10-03)

 * fix test dependencies resolving in compiler and decompiler maven

**1.0** (2013-10-01)

 * initial public version
