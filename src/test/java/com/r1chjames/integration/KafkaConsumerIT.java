package com.r1chjames.integration;

import com.r1chjames.kafka.KafkaConsumer;
import com.salesforce.kafka.test.KafkaTestUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.awaitility.Awaitility.await;

public final class KafkaConsumerIT extends KafkaTest {

    private KafkaTestUtils kafkaTestUtils;
    private static final String TOPIC = "topic-1";
    private static final int POLL_SECONDS = 5;

    @BeforeEach
    void setUp() {
        kafkaTestUtils = TEST_KAFKA.getKafkaTestUtils();
        kafkaTestUtils.createTopic(TOPIC, 1, (short) 1);
        resetConsumedRecords();
    }

    private KafkaConsumer createConsumer(final boolean shouldProcessFromBeginning) {
        return KafkaConsumer.builder()
            .topics(List.of(TOPIC).toArray(new String[0]))
            .props(localKafkaProps(kafkaTestUtils.describeClusterNodes()))
            .pollPeriod(Duration.ofSeconds(POLL_SECONDS))
            .shouldProcessFromBeginning(shouldProcessFromBeginning)
            .messageHandler(testMessageHandler())
            .build();
    }

    @Test
    void testConsumeFromBeginning() {
        kafkaTestUtils.produceRecords(
            Map.of("key1".getBytes(), "Sample message".getBytes()),
            TOPIC,
            0
        );

        createConsumer(true).run();
        await().atMost(ASSERT_WAIT_SECONDS, SECONDS).until(() -> !getConsumedRecords().isEmpty());

        assertThat(getConsumedRecords())
            .isNotNull()
            .hasSize(1)
            .extracting(ConsumerRecord::topic, ConsumerRecord::key, ConsumerRecord::value)
            .containsExactly(tuple("topic-1", "key1", "Sample message"));
    }
}
