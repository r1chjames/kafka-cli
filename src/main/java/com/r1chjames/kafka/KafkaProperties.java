package com.r1chjames.kafka;

import com.r1chjames.cli.CommandLineParser;
import org.apache.commons.cli.CommandLine;

import java.util.Properties;

import static com.r1chjames.cli.CommandLineOptions.*;
import static com.r1chjames.cli.CommandLineOptions.SCHEMA_REGISTRY;
import static com.r1chjames.kafka.EnhancedConsumerConfig.SCHEMA_REGISTRY_URL;
import static org.apache.kafka.clients.consumer.ConsumerConfig.*;
import static org.apache.kafka.clients.consumer.ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG;

public class KafkaProperties {

    public Properties parsedConfig(final CommandLineParser commandLineParser, final CommandLine commandLine) {
        return new Properties() {{
            put(GROUP_ID_CONFIG, commandLineParser.safeGetStringOptionValue(commandLine, GROUP_ID));
            put(KEY_DESERIALIZER_CLASS_CONFIG, commandLineParser.safeGetStringOptionValue(commandLine, DEFAULT_KEY_DESERIALIZER));
            put(VALUE_DESERIALIZER_CLASS_CONFIG, ErrorTolerantKafkaAvroDeserializer.class);
            put(BOOTSTRAP_SERVERS_CONFIG, commandLineParser.safeGetStringOptionValue(commandLine, BOOTSTRAP_SERVERS));
            put(SCHEMA_REGISTRY_URL, commandLineParser.safeGetStringOptionValue(commandLine, SCHEMA_REGISTRY));
        }};
    }
}
