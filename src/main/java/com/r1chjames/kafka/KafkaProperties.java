package com.r1chjames.kafka;

import com.r1chjames.cli.CommandLineConstants;
import picocli.CommandLine;

import java.util.Properties;

import static com.r1chjames.cli.CommandLineConstants.*;
import static com.r1chjames.kafka.EnhancedConsumerConfig.SCHEMA_REGISTRY_URL;
import static java.lang.System.currentTimeMillis;
import static org.apache.kafka.clients.consumer.ConsumerConfig.*;

@picocli.CommandLine.Command
public class KafkaProperties {

    @picocli.CommandLine.Option(names = {GROUP_ID, GI},
            description = "Consumer group ID")
    private String groupId = String.valueOf(currentTimeMillis());

    @picocli.CommandLine.Option(names = {BOOTSTRAP_SERVERS, BS},
            description = "Comma-separated list of Kafka broker addresses",
            required = true)
    private String bootstrapServers;

    @picocli.CommandLine.Option(names = {CommandLineConstants.SCHEMA_REGISTRY, SR},
            description = "Schema registry URL",
            defaultValue = "")
    private String schemaRegistry;

    @picocli.CommandLine.Option(names = {DEFAULT_KEY_DESERIALIZER, DKD},
            description = "Default key deserializer class",
            defaultValue = "org.apache.kafka.common.serialization.StringDeserializer")
    private String defaultKeyDeserializer = "org.apache.kafka.common.serialization.StringDeserializer";

    @CommandLine.Option(names = {DEFAULT_VALUE_DESERIALIZER, DVD},
            description = "Default value deserializer class",
            defaultValue = "com.r1chjames.kafka.ErrorTolerantKafkaAvroDeserializer")
    private String defaultValueDeserializer = "com.r1chjames.kafka.ErrorTolerantKafkaAvroDeserializer";

    public final Properties parsedConfig() throws ClassNotFoundException {
        return new Properties() {{
            put(GROUP_ID_CONFIG, groupId);
            put(KEY_DESERIALIZER_CLASS_CONFIG, Class.forName(defaultKeyDeserializer));
            put(VALUE_DESERIALIZER_CLASS_CONFIG, Class.forName(defaultValueDeserializer));
            put(BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
            put(SCHEMA_REGISTRY_URL, schemaRegistry);
        }};
    }
}
