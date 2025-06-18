package com.r1chjames.kafka;

import org.apache.avro.Schema;
import org.apache.kafka.common.errors.SerializationException;

public final class ErrorTolerantKafkaAvroDeserializer extends io.confluent.kafka.serializers.KafkaAvroDeserializer {

    @Override
    public Object deserialize(final String s, final byte[] bytes) {
        try {
            return deserialize(bytes);
        } catch (SerializationException e) {
            System.out.printf("Topic: %s | Value: %s%n", s, new String(bytes));
            return null;
        }
    }

    public Object deserialize(final String s, final byte[] bytes, final Schema readerSchema) {
        try {
            return deserialize(bytes, readerSchema);
        } catch (SerializationException e) {
            System.out.printf("Topic: %s | Value: %s | Schema: %s%n", s, new String(bytes), readerSchema);
            return null;
        }
    }
}
