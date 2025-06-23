package com.r1chjames.integration;

import com.salesforce.kafka.test.junit5.SharedKafkaTestResource;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.Node;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;

import static org.apache.kafka.clients.consumer.ConsumerConfig.*;
import static org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG;

public class KafkaTest {

    @RegisterExtension
    protected static final SharedKafkaTestResource sharedKafkaTestResource = new SharedKafkaTestResource();

    protected List<ConsumerRecord<Object, Object>> consumedRecords;

    protected Properties localKafkaProps(final List<Node> nodes) {
        return new Properties() {{
            put(BOOTSTRAP_SERVERS_CONFIG, String.format("%s:%d", nodes.getFirst().host(), nodes.getFirst().port()));
            put(GROUP_ID_CONFIG, "test-group-1");
            put(KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
            put(VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
            put(KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
            put(VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        }};
    }

    protected Consumer<ConsumerRecord<Object, Object>> testMessageHandler() {
        return record -> {
            consumedRecords.add(record);
        };
    }
}
