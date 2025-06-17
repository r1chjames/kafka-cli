package com.r1chjames.kafka;

import com.r1chjames.cli.CliParameterException;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.KafkaException;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static com.r1chjames.cli.CommandLineConstants.*;

@Builder
@RequiredArgsConstructor
@picocli.CommandLine.Command(
        name = "produce",
        description = "Produces to a Kafka topic"
)
public class KafkaProducer extends KafkaProperties implements Runnable {

    @Setter
    @picocli.CommandLine.Option(names = {TOPICS, T},
            description = "Topics to produce to",
            required = true)
    private String[] topic;

    @Setter
    @picocli.CommandLine.Option(names = {PRODUCE_COUNT, PC},
            description = "Number of records to produce",
            defaultValue = "1000")
    private int produceCount;

    @Setter
    @picocli.CommandLine.Option(names = {PRODUCE_STRING, PS},
            description = "String to produce as the message value",
            defaultValue = "Hello, Kafka!")
    private String produceString;

    private Producer<String, String> producer;
    private Properties props;

    public void init() {
        try {
            props = parsedConfig();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        try {
            producer = new org.apache.kafka.clients.producer.KafkaProducer<>(props);
        } catch (KafkaException e) {
            throw new CliParameterException(e.getMessage());
        }
    }

    @Override
    public void run() {
        init();
        sendMessages();
    }

    public void sendMessages() {

        var recordsSent = new AtomicInteger(0);

        if (topic.length > 1) {
            throw new CliParameterException("Please only provide a single topic to produce to.");
        }
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");


        try {
            IntStream.range(0, produceCount).boxed().toList().forEach(_ -> {
                System.out.println(recordsSent.incrementAndGet());
                sendStringMessage(producer, Arrays.stream(topic).findFirst().get(), produceString);
            });
        } catch (Exception e) {
            throw new CliParameterException(e.getMessage());
        }
    }


    private static void sendStringMessage(final Producer<String, String> producer, final String topic, final String value) {
        try {
            producer.send(new ProducerRecord<>(topic, value)).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        System.out.println("Sent message with value: " + value);
    }
}