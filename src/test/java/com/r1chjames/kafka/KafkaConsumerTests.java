package com.r1chjames.kafka;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class KafkaConsumerTests {

    @Mock
    private Consumer<Object, Object> consumer;

    private KafkaConsumer kafkaConsumer;

    @BeforeEach
    void beforeTests() {
        when(consumer.partitionsFor(any())).thenReturn(List.of(new PartitionInfo("test-topic", 0, null, null, null, null)));
        when(consumer.poll(any())).thenReturn(
            new ConsumerRecords<>(
                Map.of(
                    new TopicPartition("test-topic", 0), List.of(new ConsumerRecord<>("test-topic", 0, 0L, 1, "test-message"))
                )
            )
        );
        kafkaConsumer = KafkaConsumer.builder()
            .consumer(consumer)
            .topics(Stream.of("test-topic").toArray(String[]::new))
            .shouldProcessFromBeginning(true)
            .build();
    }

    @Test
    void testSendWithValidParams() {
        assertDoesNotThrow(() -> kafkaConsumer.pollForRecords());
        verify(consumer, times(1)).poll(any());
    }

    @Test
    void testSendConsumeFromBeginning() {
        assertDoesNotThrow(() -> kafkaConsumer.pollForRecords());
        verify(consumer, times(1)).partitionsFor(any());
        verify(consumer, times(1)).poll(any());
    }
}
