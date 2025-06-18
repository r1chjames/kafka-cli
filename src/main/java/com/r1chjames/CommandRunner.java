package com.r1chjames;

import com.r1chjames.cli.CliParameterException;
import picocli.CommandLine;

public final class CommandRunner {

    public static void execute(final String[] args) {
        final var commandLine = new CommandLine(new SubcommandsMethods());
        commandLine.execute(args);
        commandLine.getCommandSpec().parser().collectErrors(true);
        var parseResult = commandLine.parseArgs(args);
        var parseErrors = parseResult.errors();
        if (!parseErrors.isEmpty()) {
            throw new CliParameterException("Invalid command line arguments. Please check your input." + parseErrors);
        }
    }
}
