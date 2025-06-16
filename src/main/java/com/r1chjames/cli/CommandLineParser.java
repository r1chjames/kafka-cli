package com.r1chjames.cli;

import org.apache.commons.cli.*;

import java.util.Arrays;

import static com.r1chjames.cli.CommandLineOptions.*;

public class CommandLineParser {

    public CommandLine parse(final String[] args) {

        final var options = new Options();

        options.addOption(createRequiredOption(MODE));
        options.addOption(createRequiredOption(SHOULD_PROCESS_FROM_BEGINNING));
        options.addOption(createRequiredOption(TOPICS));
        options.addOption(createRequiredOption(BOOTSTRAP_SERVERS));
        options.addOption(createRequiredOption(SCHEMA_REGISTRY));
        options.addOption(createRequiredOption(DEFAULT_KEY_DESERIALIZER));
        options.addOption(createRequiredOption(DEFAULT_VALUE_DESERIALIZER));
        options.addOption(createRequiredOption(PRODUCE_COUNT));
        options.addOption(createRequiredOption(PRODUCE_STRING));

        final var parser = new DefaultParser();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println("Error parsing command line options: " + e.getMessage());
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("myApp", options);
        }

        return cmd;
    }

    private Option createRequiredOption(final CommandLineOptions commandLineOptions) {
        return createRequiredOption(
                commandLineOptions.getShortOpt(),
                commandLineOptions.getLongOpt(),
                commandLineOptions.isRequired(),
                commandLineOptions.getDescription());
    }

    private Option createRequiredOption(final String opt, final String longOpt, final boolean hasArg, final String description) {
        Option option = new Option(opt, longOpt, hasArg, description);
        option.setRequired(true);
        return option;
    }

    public String safeGetStringOptionValue(final CommandLine cmd, final CommandLineOptions option) {
        return Arrays.stream(cmd.getOptions())
                .filter(o -> o.getOpt().equals(option.getLongOpt()))
                .map(Option::getValue)
                .map(String::valueOf)
                .findFirst()
                .orElse((String) option.getDefaultValue());
    }

    public Integer safeGetIntOptionValue(final CommandLine cmd, final CommandLineOptions option) {
        return Arrays.stream(cmd.getOptions())
                .filter(o -> o.getOpt().equals(option.getLongOpt()))
                .map(Option::getValue)
                .map(Integer::parseInt)
                .findFirst()
                .orElse((Integer) option.getDefaultValue());
    }

    public Boolean safeGetBooleanOptionValue(final CommandLine cmd, final CommandLineOptions option) {
        return Arrays.stream(cmd.getOptions())
                .filter(o -> o.getOpt().equals(option.getLongOpt()))
                .map(Option::getValue)
                .map(Boolean::parseBoolean)
                .findFirst()
                .orElse((Boolean) option.getDefaultValue());
    }

}
