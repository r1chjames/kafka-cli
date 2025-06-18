package com.r1chjames;

import com.r1chjames.cli.CliParameterException;
import com.r1chjames.kafka.KafkaConsumer;
import com.r1chjames.kafka.KafkaProducer;
import picocli.CommandLine;

public final class KafkaCli {

    public static void main(final String[] args) {

        try {
            CommandRunner.execute(args);
        } catch (final CommandLine.ParameterException | CliParameterException ex) {
            System.err.println("Error: " + ex.getMessage());
            System.err.println("Use --help for usage information.");
            System.exit(1);
        } catch (final Exception ex) {
            System.err.println("Unexpected error: " + ex.getMessage());
            ex.printStackTrace();
            System.exit(2);
        }
        System.exit(0);
    }
}

@CommandLine.Command(
    mixinStandardHelpOptions = true,
    subcommands = {
        KafkaProducer.class,
        KafkaConsumer.class
    }
)
final class SubcommandsMethods implements Runnable {

    @Override
    public void run() {
        throw new CliParameterException("Subcommand needed: 'consume' or 'produce'");
    }

}
