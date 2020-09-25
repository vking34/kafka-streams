#!/bin/bash

# create user-data topic
bin/kafka-topics.sh --create \
--topic user-data \
--bootstrap-server localhost:9092 \
--replication-factor 1 --partitions 2

# create user-purchases topic
bin/kafka-topics.sh --create \
--topic user-purchases \
--bootstrap-server localhost:9092 \
--replication-factor 1 --partitions 3 \
--config cleanup.policy=compact

# create inner join result output topic
bin/kafka-topics.sh --create \
--topic user-purchases-enriched-inner-join \
--bootstrap-server localhost:9092 \
--replication-factor 1 --partitions 3

# create left join result output topic
bin/kafka-topics.sh --create \
--topic user-purchases-enriched-left-join \
--bootstrap-server localhost:9092 \
--replication-factor 1 --partitions 3

# create consumer for inner join result
bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 \
    --topic user-purchases-enriched-inner-join \
    --from-beginning \
    --formatter kafka.tools.DefaultMessageFormatter \
    --property print.key=true \
    --property print.value=true \
    --property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer \
    --property value.deserializer=org.apache.kafka.common.serialization.StringDeserializer

# create consumer for left join result
bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 \
    --topic user-purchases-enriched-left-join \
    --from-beginning \
    --formatter kafka.tools.DefaultMessageFormatter \
    --property print.key=true \
    --property print.value=true \
    --property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer \
    --property value.deserializer=org.apache.kafka.common.serialization.StringDeserializer




