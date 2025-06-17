package com.r1chjames.kafka;

import com.r1chjames.cli.CliParameterException;
import lombok.Builder;
import lombok.Setter;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
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
@picocli.CommandLine.Command(
        name = "consume",
        description = "Consumed from a Kafka topic")
public class KafkaConsumer extends KafkaProperties implements Runnable {

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

    private void init() {
        try {
            props = parsedConfig();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        try {
            consumer = new org.apache.kafka.clients.consumer.KafkaConsumer<>(props);
        } catch (KafkaException e) {
            throw new CliParameterException(e.getMessage());
        }

    }

    @Override
    public void run() {
        init();
        pollForRecords();
    }

    protected void pollForRecords() {
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
            System.out.println("Consumer closed.");
        }
    }
}

