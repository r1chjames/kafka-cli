package com.r1chjames.integration;

import com.r1chjames.kafka.KafkaProducer;
import com.salesforce.kafka.test.KafkaTestUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;

public final class KafkaProducerIT extends KafkaTest {

    private KafkaTestUtils kafkaTestUtils;
    private static final String TOPIC = "topic-1";

    @BeforeEach
    void setUp() {
        kafkaTestUtils = TEST_KAFKA.getKafkaTestUtils();
        kafkaTestUtils.createTopic(TOPIC, 1, (short) 1);
    }

    private KafkaProducer createProducer(final String message, final int count) {
        return KafkaProducer.builder()
            .topic(TOPIC)
            .props(localKafkaProps(kafkaTestUtils.describeClusterNodes()))
            .produceCount(count)
            .produceString(message)
            .build();
    }

    @Test
    void testProduce() {
        final var count = 5;
        createProducer("Sample message", count).run();
        final var records = kafkaTestUtils.consumeAllRecordsFromTopic(TOPIC);

        assertThat(records)
            .isNotNull()
            .hasSize(count)
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
