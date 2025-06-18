package com.r1chjames.kafka;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public final class KafkaConsumerTests {

    @Mock
    private Consumer<Object, Object> consumer;

    @Mock
    private Properties props;

    private KafkaConsumer kafkaConsumer;

    @BeforeEach
    void beforeTests() {
        when(consumer.partitionsFor(any()))
            .thenReturn(List.of(new PartitionInfo("test-topic", 0, null, null, null, null)));
        when(consumer.poll(any())).thenReturn(
                new ConsumerRecords<>(
                    Map.of(
                        new TopicPartition("test-topic", 0),
                        List.of(new ConsumerRecord<>("test-topic", 0, 0L, 1, "test-message"))
                    )
                )
            )
            .thenReturn(null);
        kafkaConsumer = KafkaConsumer.builder()
            .consumer(consumer)
            .props(props)
            .topics(Stream.of("test-topic").toArray(String[]::new))
            .shouldProcessFromBeginning(true)
            .build();
    }

    @Test
    void testSendWithValidParams() {
        assertDoesNotThrow(() -> kafkaConsumer.pollForRecords());
        verify(consumer, times(2)).poll(any());
    }

    @Test
    void testSendConsumeFromBeginning() {
        assertDoesNotThrow(() -> kafkaConsumer.pollForRecords());
        verify(consumer, times(1)).partitionsFor(any());
        verify(consumer, times(2)).poll(any());
    }
}
