package com.r1chjames.kafka;

import org.apache.kafka.clients.producer.Producer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class KafkaProducerTests {

    @Mock
    private Producer<String, String> producer;

    private KafkaProducer kafkaProducer;

    @BeforeEach
    void beforeTests() {
        when(producer.send(any())).thenReturn(null);
        kafkaProducer = KafkaProducer.builder()
                .producer(producer)
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
