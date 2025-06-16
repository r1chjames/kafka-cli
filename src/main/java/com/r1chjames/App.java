package com.r1chjames;

import com.r1chjames.cli.CommandLineParser;
import com.r1chjames.kafka.AvroConsumer;
import com.r1chjames.kafka.AvroProducer;

public class App {

    public static void main(String args[]) {

        final var commands = new CommandLineParser().parse(args);
        final var appRunner = new AppRunner();

        switch (commands.getOptionValue("mode")) {
            case "consumer":
                appRunner.submit(() -> new AvroConsumer().consume(commands));
                break;
            case "producer":
                appRunner.submit(() -> new AvroProducer().produce(commands));
                break;
            default:
                System.err.println("Invalid mode specified. Use 'consumer' or 'producer'.");
                System.exit(1);
        }
    }

}

