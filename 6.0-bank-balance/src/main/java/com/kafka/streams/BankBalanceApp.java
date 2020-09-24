package com.kafka.streams;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.confluent.kafka.serializers.KafkaJsonSerializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.*;
import org.apache.kafka.connect.json.JsonDeserializer;
import org.apache.kafka.connect.json.JsonSerializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;

import java.io.IOException;
import java.time.Instant;
import java.util.Properties;


public class BankBalanceApp {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();


    public static void main (String[] args){
        Serializer<JsonNode> jsonSerializer = new JsonSerializer();
        Deserializer<JsonNode> jsonDeserializer = new JsonDeserializer();
        Serde<JsonNode> jsonSerde = Serdes.serdeFrom(jsonSerializer, jsonDeserializer);
        System.out.println(System.getenv("APPLICATION_ID_CONFIG"));
        Properties config = new Properties();
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, System.getenv("APPLICATION_ID_CONFIG"));
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, System.getenv("BOOTSTRAP_SERVERS_CONFIG"));
        config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, jsonSerde);
        config.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, "0");
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");


        StreamsBuilder builder = new StreamsBuilder();

        // step 1: Read from topic
        KStream<String, JsonNode> transactionKStream = builder.stream(System.getenv("TOPIC"), Consumed.with(Serdes.String(), jsonSerde));

        ObjectNode initialBalance = JsonNodeFactory.instance.objectNode();
        initialBalance.put("count", 0);
        initialBalance.put("balance", 0);
        initialBalance.put("count","");

        KTable<String, JsonNode> bankBalance = transactionKStream
                .groupByKey()
                .aggregate(
                        () -> initialBalance,
                        (key, transaction, balance) -> newBalance(transaction, balance),
                        Materialized.as("bank-balance-store").withValueSerde(jsonSerde)
                        );

        bankBalance.toStream().to("bank-transactions-output");
        KafkaStreams streams = new KafkaStreams(builder.build(), config);
        streams.cleanUp();
        streams.start();
//        streams.localThreadsMetadata().forEach(System.out::println);

        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }

    private static ObjectNode newBalance(JsonNode transaction, JsonNode balance){
        ObjectNode newBalance = JsonNodeFactory.instance.objectNode();

        newBalance.put("count", balance.get("count").asInt() + 1);
        newBalance.put("balance", balance.get("balance").asInt() + transaction.get("balance").asInt());
        newBalance.put("time", transaction.get("time").asText());

        return newBalance;
    }
}
