#!/bin/bash

./bin/kafka-topics.sh --create --topic streams-plaintext-input --bootstrap-server localhost:9092

bin/kafka-topics.sh --create --topic streams-wordcount-output --bootstrap-server localhost:9092

bin/kafka-console-producer.sh --topic streams-plaintext-input --bootstrap-server localhost:9092

bin/kafka-console-consumer.sh --topic streams-plaintext-input --from-beginning --bootstrap-server localhost:9092

bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 \
    --topic streams-wordcount-output \
    --from-beginning \
    --formatter kafka.tools.DefaultMessageFormatter \
    --property print.key=true \
    --property print.value=true \
    --property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer \
    --property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer


bin/kafka-run-class.sh org.apache.kafka.streams.examples.wordcount.WordCountDemo

