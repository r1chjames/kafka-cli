package com.r1chjames.kafka;

import com.r1chjames.cli.CommandLineParser;
import org.apache.commons.cli.CommandLine;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.SerializationException;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static com.r1chjames.cli.CommandLineOptions.*;

public class AvroConsumer {


    public void consume(final CommandLine commandLine) {

        final CommandLineParser commandLineParser = new CommandLineParser();

        final var shouldProcessFromBeginning = commandLineParser.safeGetBooleanOptionValue(commandLine, SHOULD_PROCESS_FROM_BEGINNING);
        final var topics = Arrays.stream(commandLineParser.safeGetStringOptionValue(commandLine, TOPICS).split(",")).toList();

        final var props = new KafkaProperties().parsedConfig(commandLineParser, commandLine);
        pollForRecords(props, topics, shouldProcessFromBeginning);
    }

    private static void pollForRecords(final Properties props, final List<String> topics, final boolean shouldProcessFromBeginning) {
        final var consumer = new KafkaConsumer<>(props);

        try(consumer) {

            final var topicPartitions = topics
                    .stream()
                    .map(t -> consumer.partitionsFor(t)
                            .stream()
                            .map(p -> new TopicPartition(t, p.partition()))
                            .collect(Collectors.toList()))
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());

            System.out.printf("Subscribing to %s topics across %s partitions\n", topics.size(), topicPartitions.size());
            consumer.assign(topicPartitions);

            if (shouldProcessFromBeginning) {
                System.out.println("Seeking to beginning");
                consumer.seekToBeginning(topicPartitions);
            }

            while (true) {
                ConsumerRecords<Object, Object> records = consumer.poll(Duration.ofSeconds(1L));
                records.forEach(record -> {
                    System.out.println("------------------------------------------------------");
                    System.out.printf("Timestamp: %s\n", LocalDateTime.ofInstant(Instant.ofEpochMilli(record.timestamp()), ZoneId.systemDefault()));
                    System.out.printf("Topic: %s\n", record.topic());
                    System.out.printf("Key: %s\n", record.key());
                    System.out.printf("Value: %s\n", record.value());
                });
                consumer.commitSync();
            }
        } catch (SerializationException e) {
            System.err.println("Error deserializing records: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("An unexpected error occurred while consuming records: " + e.getMessage());
            e.printStackTrace();
        } finally {
            consumer.close();
            System.out.println("Consumer closed.");
        }
    }
}

