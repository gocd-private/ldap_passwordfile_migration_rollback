package com.thoughtworks.go;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

import java.io.PrintStream;

public class CommandLineParser {
    private final PrintStream stderr;
    private final SystemExitter exitter;

    public interface SystemExitter {
        void exit(int status);
    }

    public CommandLineParser() {
        this(System.err, new SystemExitter() {
            @Override
            public void exit(int status) {
                System.exit(status);
            }
        });
    }

    public CommandLineParser(PrintStream stderr, SystemExitter exitter) {
        this.stderr = stderr;
        this.exitter = exitter;
    }

    public void exit(int status) {
        exitter.exit(status);
    }

    public GoSettings parse(String... args) {
        GoSettings result = new GoSettings();
        try {
            new JCommander(result, args);

            if (result.help) {
                printUsageAndExit(0);
            }

            return result;
        } catch (ParameterException e) {
            stderr.println(e.getMessage());
            printUsageAndExit(1);
        }

        return null;
    }

    private void printUsageAndExit(int exitCode) {
        StringBuilder out = new StringBuilder();
        JCommander jCommander = new JCommander(new GoSettings());
        jCommander.setProgramName("java -jar rescue_from_migration_failure.jar");
        jCommander.usage(out);
        stderr.print(out);
        exit(exitCode);
    }
}
