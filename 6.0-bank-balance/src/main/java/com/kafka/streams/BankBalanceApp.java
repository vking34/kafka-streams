package com.kafka.streams;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.*;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.connect.json.JsonDeserializer;
import org.apache.kafka.connect.json.JsonSerializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;

import java.util.Properties;


public class BankBalanceApp {

    public static void main(String[] args) {

        // create json serde
        final Serializer<JsonNode> jsonSerializer = new JsonSerializer();
        final Deserializer<JsonNode> jsonDeserializer = new JsonDeserializer();
        final Serde<JsonNode> jsonSerde = Serdes.serdeFrom(jsonSerializer, jsonDeserializer);

        // streams config
        Properties config = new Properties();
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, System.getenv("APPLICATION_ID_CONFIG"));
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, System.getenv("BOOTSTRAP_SERVERS_CONFIG"));
        config.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE);  // Exactly once processing!!!
        config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        config.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, "0");    // not for production
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        // build topology
        StreamsBuilder builder = new StreamsBuilder();
        // step 1: Read from topic
        KStream<String, JsonNode> transactionKStream = builder.stream(System.getenv("TOPIC"), Consumed.with(Serdes.String(), jsonSerde));
        transactionKStream.peek((key, value) -> {   // check consumer message
            System.out.println("name:" + value.get("name") + ", amount: " + value.get("amount"));
        });

        // step 2: initialize state
        ObjectNode initialBalance = JsonNodeFactory.instance.objectNode();
        initialBalance.put("count", 0);
        initialBalance.put("balance", 0L);
        initialBalance.put("count", "");

        // step 3: aggregate
        KTable<String, JsonNode> bankBalance = transactionKStream
                .groupByKey()
                .aggregate(
                        () -> initialBalance,
                        (key, transaction, balance) -> newBalance(transaction, balance),
                        Materialized.<String, JsonNode, KeyValueStore<Bytes, byte[]>>as("bank-balance-store")
                                .withKeySerde(Serdes.String())
                                .withValueSerde(jsonSerde)
                );

        // step 4: write into output topic
        bankBalance.toStream().to("bank-transactions-output", Produced.with(Serdes.String(), jsonSerde));

        KafkaStreams streams = new KafkaStreams(builder.build(), config);
        streams.cleanUp();
        streams.start();
//        streams.localThreadsMetadata().forEach(System.out::println);

        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }

    private static ObjectNode newBalance(JsonNode transaction, JsonNode balance) {
        ObjectNode newBalance = JsonNodeFactory.instance.objectNode();

        newBalance.put("count", balance.get("count").asInt() + 1);
        newBalance.put("balance", balance.get("balance").asLong() + transaction.get("amount").asLong());
        newBalance.put("time", transaction.get("time").asText());

        return newBalance;
    }
}
