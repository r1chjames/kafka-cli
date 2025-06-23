package com.r1chjames.integration;

import com.r1chjames.kafka.KafkaConsumer;
import com.salesforce.kafka.test.KafkaTestUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.awaitility.Awaitility.await;

public class KafkaConsumerIT extends KafkaTest {

    private KafkaTestUtils kafkaTestUtils;
    private static final String TOPIC = "topic-1";

    @BeforeEach
    void setUp() {
        kafkaTestUtils = sharedKafkaTestResource.getKafkaTestUtils();
        kafkaTestUtils.createTopic(TOPIC, 1, (short) 1);
        consumedRecords = new ArrayList<>();
    }

    private KafkaConsumer createConsumer(final boolean shouldProcessFromBeginning) {
        return KafkaConsumer.builder()
            .topics(List.of(TOPIC).toArray(new String[0]))
            .props(localKafkaProps(kafkaTestUtils.describeClusterNodes()))
            .pollPeriod(Duration.ofSeconds(5)) // A short poll period for tests is good
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
        await().atMost(5, SECONDS).until(() -> !consumedRecords.isEmpty());

        assertThat(consumedRecords)
            .isNotNull()
            .hasSize(1)
            .extracting(ConsumerRecord::topic, ConsumerRecord::key, ConsumerRecord::value)
            .containsExactly(tuple("topic-1", "key1", "Sample message"));
    }
}
