package com.r1chjames.cli;

import static java.lang.System.currentTimeMillis;

public enum CommandLineOptions {

    MODE("m", "mode", true, "true", "Whether to process messages from the beginning of the topic"),
    SHOULD_PROCESS_FROM_BEGINNING("sr", "shouldProcessFromBeginning", true,true, "Whether to process messages from the beginning of the topic"),
    TOPICS("t", "topics", true, "topic1,topic2", "Comma-separated list of topics to consume from"),
    GROUP_ID("gid", "group_id", false, new String(String.valueOf(currentTimeMillis())), "Group id for the consumer, defaults to current timestamp"),
    BOOTSTRAP_SERVERS("bs", "bootstrap_servers", true, "", "Hostname/IPs of Kafka brokers, comma-separated"),
    SCHEMA_REGISTRY("sr", "schema_registry_url", false, "", "URL of schema registry"),
    DEFAULT_KEY_SERIALIZER("ks", "default_key_serializer", false, "io.confluent.kafka.serializers.KafkaAvroSerializer", "Default key serializer class"),
    DEFAULT_VALUE_SERIALIZER("vs", "default_value_serializer", false, "io.confluent.kafka.serializers.KafkaAvroSerializer", "Default value serializer class"),
    DEFAULT_KEY_DESERIALIZER("kd", "default_key_deserializer", false, "io.confluent.kafka.serializers.KafkaAvroDeserializer", "Default key deserializer class"),
    DEFAULT_VALUE_DESERIALIZER("vd", "default_value_serializer", false, "io.confluent.kafka.serializers.KafkaAvroDeserializer", "Default value deserializer class"),
    PRODUCE_COUNT("n", "count",false, 1, "Number of messages to produce, defaults to 1"),
    PRODUCE_STRING("st", "produce_string", false, "Test message", "String message to send when using the producer");

    private final String shortOpt;
    private final String longOpt;
    private final boolean required;
    private final Object defaultValue;
    private final String description;

    CommandLineOptions(final String shortOpt, final String longOpt, final boolean required, final Object defaultValue, final String description) {
        this.shortOpt = shortOpt;
        this.longOpt = longOpt;
        this.required = required;
        this.defaultValue = defaultValue;
        this.description = description;
    }

    public String getShortOpt() {
        return shortOpt;
    }

    public String getLongOpt() {
        return longOpt;
    }

    public boolean isRequired() {
        return required;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public String getDescription() {
        return description;
    }
}
