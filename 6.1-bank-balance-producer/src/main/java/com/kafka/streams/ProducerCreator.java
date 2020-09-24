package com.kafka.streams;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class ProducerCreator {
    public static Producer<String, String> createProducer() {
        Properties properties = new Properties();

        // producer properties
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, System.getenv("KAFKA_BOOTSTRAP_SERVERS"));
//        properties.put(ProducerConfig.CLIENT_ID_CONFIG, System.getenv("CLIENT_ID"));
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        // safe producer
        properties.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
        properties.put(ProducerConfig.ACKS_CONFIG, "all");  // strongest producing guarantee
        properties.put(ProducerConfig.RETRIES_CONFIG, "3");
        properties.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "5");

        // high through
        properties.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");
        properties.put(ProducerConfig.LINGER_MS_CONFIG, "1");
//        properties.put(ProducerConfig.BATCH_SIZE_CONFIG, Integer.toString(32*1024));

        return new KafkaProducer<String, String>(properties);
    }
}
