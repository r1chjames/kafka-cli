package com.r1chjames.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;

import java.util.Map;
import java.util.Properties;

public class EnhancedConsumerConfig extends ConsumerConfig {

    public static final String SCHEMA_REGISTRY_URL = "schema.registry.url";

    public EnhancedConsumerConfig(final Properties props) {
        super(props);
    }

    public EnhancedConsumerConfig(final Map<String, Object> props) {
        super(props);
    }

    protected EnhancedConsumerConfig(final Map<?, ?> props, final boolean doLog) {
        super(props, doLog);
    }
}
