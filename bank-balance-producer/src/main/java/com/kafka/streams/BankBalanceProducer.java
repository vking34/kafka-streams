package com.kafka.streams;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Future;


public class BankBalanceProducer {
    private final static String TOPIC = System.getenv("TOPIC");
    private final static List<String> USERS = Arrays.asList("John", "Vuong", "Travis");
    private final static Integer USERS_LENGTH = USERS.size();
    private final static List<Integer> AMOUNTS = Arrays.asList(2000, 3000, 250, 10000);
    private final static Integer AMOUNTS_SIZE = AMOUNTS.size();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @SneakyThrows
    public static void main(String[] args) throws InterruptedException {
        Random random = new Random();
        Producer<String, String> producer = ProducerCreator.createProducer();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println(producer);
            producer.close();
        }));

        System.out.println(producer);
        while (true){
            int randomUserIndex = random.nextInt(USERS_LENGTH);
            int randomAmountIndex = random.nextInt(AMOUNTS_SIZE);
            String time = LocalDateTime.now().toString();
            String username = USERS.get(randomUserIndex);
            Integer amount = AMOUNTS.get(randomAmountIndex);
            String transactionStr = OBJECT_MAPPER.writeValueAsString(new Transaction(username, amount.longValue(), time));
            System.out.println(transactionStr);

            ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC, username, transactionStr);
            Future<RecordMetadata> metadata = producer.send(record);
            Thread.sleep(2000);
        }
    }
}
