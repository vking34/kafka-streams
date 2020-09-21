package com.kafka.streams;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.common.serialization.Serdes;

import java.util.Arrays;
import java.util.Properties;

public class FavoriteColorApp {
    public static void main(String[] args){
        Properties config = new Properties();
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, "favorite-color-app");
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        config.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, "0");

        StreamsBuilder builder = new StreamsBuilder();

        // step 1: filter stream
        KStream<String, String> textLines = builder.stream("favorite-color-input");
        KStream<String, String> usersAndColors = textLines
                .filter((key, value) -> value.contains(","))
                .selectKey((key, value) -> value.split(",")[0].toLowerCase())
                .mapValues(value -> value.split(",")[1].toLowerCase())
                .filter((user, color) -> Arrays.asList("blue", "green", "red").contains(color));

        // step 2: write stream to a compacted topic
        usersAndColors.to("user-keys-and-colors");

        // step 3: read back the compacted topic as a table
        KTable<String, String> usersAndColorsTable = builder.table("user-keys-and-colors");

        // step 4: group and count
        KTable<String, Long> favoriteColors = usersAndColorsTable
                .groupBy((user, color) -> new KeyValue<>(color, color))
                .count();

        // step 5: write to the output topic
        favoriteColors.toStream().to("favorite-color-output", Produced.with(Serdes.String(), Serdes.Long()));

        KafkaStreams streams = new KafkaStreams(builder.build(), config);
        streams.cleanUp();
        streams.start();
        streams.localThreadsMetadata().forEach(System.out::println);

        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }
}
