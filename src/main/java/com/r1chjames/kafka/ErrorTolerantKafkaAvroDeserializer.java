package com.r1chjames.kafka;

import org.apache.avro.Schema;
import org.apache.kafka.common.errors.SerializationException;

public class ErrorTolerantKafkaAvroDeserializer extends io.confluent.kafka.serializers.KafkaAvroDeserializer {

    @Override
    public Object deserialize(String s, byte[] bytes) {
        try {
            return deserialize(bytes);
        } catch (SerializationException e) {
            System.out.printf("Topic: %s | Value: %s%n", s, new String(bytes));
            return null;
        }
    }

    public Object deserialize(String s, byte[] bytes, Schema readerSchema) {
        try {
            return deserialize(bytes, readerSchema);
        } catch (SerializationException e) {
            System.out.printf("Topic: %s | Value: %s | Schema: %s%n", s, new String(bytes), readerSchema);
            return null;
        }
    }
}
