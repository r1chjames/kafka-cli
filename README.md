# Kafka CLI

Kafka CLI tool for producing or consuming messages to/from a Kafka topic.

***

### Setup
* The following parameters can be passed when running the application:

| Variable                   | Short | Description                                                                                                             | Example                            |
|----------------------------|-------|-------------------------------------------------------------------------------------------------------------------------|------------------------------------|
| mode                       | m     | Either `produce` or `consume`                                                                                           | produce                            |
| shouldProcessFromBeginning | fb    | Whether consumer should start from the beginning or continue from the last offset only works if provided with group_id. | true                               |
| topics                     | t     | Which topic(s) to produce or consumer from. Comma separated.                                                            | topic-1,topic-2                    |
| group_id                   | gid   | The group ID used to consume. Defaults to an epoch timestamp if not supplied.                                           | my-group-id                        |
| bootstrap_servers          | bs    | The broker url to use along with port                                                                                   | broker:9092                        |
| schema_registry_url        | sr    | The schema registry to read schemas from. Used in consume mode.                                                         | http://schema-registry:8081        |
| default_key_deserializer   | kd    | The key deserializer. Defaults to ``.                                                                                   | StringDeserializer                 |
| default_value_deserializer | vd    | The value deserializer. Defaults to `ErrorTolerantKafkaAvroDeserializer`.                                               | ErrorTolerantKafkaAvroDeserializer |
| count                      | n     | The number of messages to produce or consume. Defaults to 1.                                                            | 10                                 |
| produce_string             | ps    | The string to produce.                                                                                                  | Test message                       |