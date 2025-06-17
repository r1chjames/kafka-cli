package com.r1chjames.kafka;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import scala.concurrent.Future;

import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class KafkaProducerTests {

    @Mock
    private Producer<String, String> producer;

    @Mock
    private Properties props;

    private KafkaProducer kafkaProducer;

    @BeforeEach
    void beforeTests() {
        when(producer.send(any())).thenReturn(CompletableFuture.completedFuture(new RecordMetadata(new TopicPartition("test-topic", 0), 0L, 0, 0L, 0, 0)));
        kafkaProducer = KafkaProducer.builder()
            .producer(producer)
            .props(props)
            .topic(Stream.of("test-topic").toArray(String[]::new))
            .produceCount(1)
            .produceString("test")
            .build();
    }

    @Test
    void testSendWithValidParams() {
        assertDoesNotThrow(() -> kafkaProducer.sendMessages());
        verify(producer, times(1)).send(any());
    }

    @Test
    void testSendMultipleMessages() {
        kafkaProducer.setProduceCount(10);
        assertDoesNotThrow(() -> kafkaProducer.sendMessages());
        verify(producer, times(10)).send(any());
    }
}
