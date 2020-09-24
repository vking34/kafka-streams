#!/bin/bash
# create input topic with one partition to get full ordering
bin/kafka-topics.sh --create \
--topic bank-transactions \
--bootstrap-server localhost:9092 \
--replication-factor 1 --partitions 1

# create output topic
bin/kafka-topics.sh --create \
--topic bank-transactions-output \
--bootstrap-server localhost:9092 \
--replication-factor 1 --partitions 1 \
--config cleanup.policy=compact

# show result
bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 \
--topic bank-transactions-output \
--formatter kafka.tools.DefaultMessageFormatter \
--property print.key=true \
--property print.value=true \
--property parse.key=true \
--property key.separator=, \
--property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer \
--property value.deserializer=org.apache.kafka.connect.json.JsonDeserializer

# start stream processor application (bank-balance)
# start producer (bank-balance-producer)
# check result