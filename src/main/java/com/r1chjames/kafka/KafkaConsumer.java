package com.r1chjames.kafka;

import com.r1chjames.cli.CliParameterException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.SerializationException;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static com.r1chjames.cli.CommandLineConstants.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@picocli.CommandLine.Command(
        name = "consume",
        description = "Consumed from a Kafka topic")
public final class KafkaConsumer extends KafkaProperties implements Runnable {

    @Setter
    @picocli.CommandLine.Option(names = {SHOULD_PROCESS_FROM_BEGINNING, SPFB},
            description = "Should the consumer start from the beginning of the topic",
            defaultValue = "false")
    private boolean shouldProcessFromBeginning;

    @Setter
    @picocli.CommandLine.Option(names = {TOPICS, T},
            description = "Comma-separated list of topics to consume from",
            required = true)
    private String[] topics;

    private Consumer<Object, Object> consumer;
    private Properties props;
    private java.util.function.Consumer<ConsumerRecord<Object, Object>> messageHandler;
    private Duration pollPeriod;

    private void init() {
        if (props == null) {
            try {
                props = parsedConfig();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        if (messageHandler == null) {
            messageHandler = defaultMessageHandler();
        }

        if (consumer == null) {
            try {
                consumer = new org.apache.kafka.clients.consumer.KafkaConsumer<>(props);
            } catch (KafkaException e) {
                throw new CliParameterException(e.getMessage());
            }
        }
    }

    @Override
    public void run() {
        init();
        pollForRecords();
    }

    void pollForRecords() {
        try {
            final var topicPartitions = Arrays.stream(topics)
                    .map(t -> consumer.partitionsFor(t)
                            .stream()
                            .map(p -> new TopicPartition(t, p.partition()))
                            .collect(Collectors.toList()))
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());

            System.out.printf("Subscribing to %s topics across %s partitions\n", topics.length, topicPartitions.size());
            consumer.assign(topicPartitions);

            if (shouldProcessFromBeginning) {
                System.out.println("Seeking to beginning");
                consumer.seekToBeginning(topicPartitions);
            }

            if (pollPeriod == null || pollPeriod.isNegative()) {
                // Case 1: Run indefinitely until the application is stopped.
                System.out.println("Polling for records indefinitely...");
                // A proper shutdown hook would be needed to break this loop gracefully.
                while (true) {
                    poll();
                }
            } else {
                // Case 2: Run for the specified duration.
                System.out.printf("Polling for records for a duration of %s...\n", pollPeriod);
                Instant endTime = Instant.now().plus(pollPeriod);

                while (Instant.now().isBefore(endTime)) {
                    poll();
                }
                System.out.println("Finished polling after the specified duration.");
            }
        } catch (SerializationException e) {
            System.err.println("Error deserializing records: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("An unexpected error occurred while consuming records: " + e.getMessage());
            e.printStackTrace();
        } finally {
            System.out.println("Consumer closed.");
        }
    }

    private void poll() {
        consumer.poll(Duration.ofSeconds(1L)).forEach(record -> messageHandler.accept(record));
        consumer.commitSync();
    }

    private java.util.function.Consumer<ConsumerRecord<Object, Object>> defaultMessageHandler() {
        return record -> {
            System.out.println("------------------------------------------------------");
            System.out.printf("Timestamp: %s\n", readableTimestampRecordTimestamp(record.timestamp()));
            System.out.printf("Topic: %s\n", record.topic());
            System.out.printf("Key: %s\n", record.key());
            System.out.printf("Value: %s\n", record.value());
        };
    }

    private LocalDateTime readableTimestampRecordTimestamp(final long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
    }
}

