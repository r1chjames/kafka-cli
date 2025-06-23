package com.r1chjames.integration;

import com.r1chjames.kafka.KafkaProducer;
import com.salesforce.kafka.test.KafkaTestUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;

public class KafkaProducerIT extends KafkaTest {

    private KafkaTestUtils kafkaTestUtils;
    private static final String TOPIC = "topic-1";

    @BeforeEach
    void setUp() {
        // The KAFKA resource is static and shared, but we can get a fresh
        // test utility instance for each test.
        kafkaTestUtils = sharedKafkaTestResource.getKafkaTestUtils();
        kafkaTestUtils.createTopic(TOPIC, 1, (short) 1);
    }

    private KafkaProducer createProducer(final String message) {
        return KafkaProducer.builder()
            .topic(TOPIC)
            .props(localKafkaProps(kafkaTestUtils.describeClusterNodes()))
            .produceCount(5)
            .produceString(message)
            .build();
    }

    @Test
    void testProduce() {
        createProducer("Sample message").run();
        final var records = kafkaTestUtils.consumeAllRecordsFromTopic(TOPIC);

        assertThat(records)
            .isNotNull()
            .hasSize(5)
            .extracting(ConsumerRecord::topic, ConsumerRecord::key, ConsumerRecord::value)
            .containsExactly(
                tuple(TOPIC, "key_0".getBytes(), "Sample message".getBytes()),
                tuple(TOPIC, "key_1".getBytes(), "Sample message".getBytes()),
                tuple(TOPIC, "key_2".getBytes(), "Sample message".getBytes()),
                tuple(TOPIC, "key_3".getBytes(), "Sample message".getBytes()),
                tuple(TOPIC, "key_4".getBytes(), "Sample message".getBytes())
            );
    }
}
