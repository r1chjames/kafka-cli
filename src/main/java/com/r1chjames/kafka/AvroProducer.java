package com.r1chjames.kafka;

import com.r1chjames.cli.CommandLineParser;
import org.apache.commons.cli.CommandLine;
import org.apache.kafka.clients.producer.*;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static com.r1chjames.cli.CommandLineOptions.*;

public class AvroProducer {

    final CommandLineParser commandLineParser = new CommandLineParser();

    public void produce(final CommandLine commandLine) {

        final var recordsToSend = commandLineParser.safeGetIntOptionValue(commandLine, PRODUCE_COUNT);
        final var produceMessage = commandLineParser.safeGetStringOptionValue(commandLine, PRODUCE_STRING);
        var recordsSent = new AtomicInteger(0);
        final var topics = Arrays.stream(commandLineParser.safeGetStringOptionValue(commandLine, TOPICS).split(",")).toList();

        if (topics.size() > 1) {
            System.err.println("Please only provide a single topic to produce to.");
            return;
        }

        final var props = new KafkaProperties().parsedConfig(commandLineParser, commandLine);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        final Producer<String, String> producer = new KafkaProducer<>(props);

        try (producer) {
            IntStream.range(0, recordsToSend).boxed().toList().forEach(_ -> {
                System.out.println(recordsSent.incrementAndGet());
                sendStringMessage(producer, topics.getFirst(), produceMessage);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static void sendStringMessage(final Producer<String, String> producer, final String topic, final String value) {
        try {
            producer.send(new ProducerRecord<>(topic, value)).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        System.out.println("Sent message with value: " + value);
    }
}