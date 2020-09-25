package com.kafka.streams;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.KStream;

import java.util.Properties;

public class UserEnrichApp {
    public static void main(String[] args){

        Properties config = new Properties();
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, System.getenv("APPLICATION_ID_CONFIG"));
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, System.getenv("BOOTSTRAP_SERVERS_CONFIG"));
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        StreamsBuilder builder = new StreamsBuilder();

        // step 1: read user data as a global table out of kafka
        GlobalKTable<String, String> usersGlobalTable = builder.globalTable("user-data");

        // step 2: read user purchases topic as KStream
        KStream<String, String> userPurchases = builder.stream("user-purchases");

        // build topology
        // step 3: inner join to enrich user purchase data
        KStream<String, String> userPurchasesEnrichedInnerJoin = userPurchases.join(
                usersGlobalTable,
                (key, value) -> key,    // map the record of the user purchase stream to the key of the GlobalKTable
                (userPurchase, userInfo) -> "Purchase=" + userPurchase + "| UserInfo=[" + userInfo + "]");

        // step 4: write the inner join result back to kafka
        userPurchasesEnrichedInnerJoin.to(System.getenv("INNER_JOIN_TOPIC"));

        // step 5: left join
        KStream<String, String> userPurchasesEnrichedLeftJoin = userPurchases.leftJoin(
                usersGlobalTable,
                (key, value) -> key,
                (userPurchase, userInfo) -> {
                    if (userInfo != null)
                        return "Purchase=" + userPurchase + "| UserInfo=[" + userInfo + "]";
                    else
                        return "Purchase=" + userPurchase + "| UserInfo=null";
                });

        // step 6: write the left join result back to kafka
        userPurchasesEnrichedLeftJoin.to(System.getenv("LEFT_JOIN_TOPIC"));

        // start streams processing
        KafkaStreams streams = new KafkaStreams(builder.build(), config);
        streams.cleanUp();
        streams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }
}
