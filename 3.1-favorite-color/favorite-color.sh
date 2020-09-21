#!/bin/bash
# create input topic with one partition to get full ordering
bin/kafka-topics.sh --create \
--topic favorite-color-input \
--bootstrap-server localhost:9092 \
--replication-factor 1 --partitions 1

# create intermediary log compacted topic
bin/kafka-topics.sh --create \
--topic user-keys-and-colors \
--bootstrap-server localhost:9092 \
--replication-factor 1 --partitions 1 \
--config cleanup.policy=compact


# create output log compacted topic
bin/kafka-topics.sh --create \
--topic favorite-color-output \
--bootstrap-server localhost:9092 \
--replication-factor 1 --partitions 1 \
--config cleanup.policy=compact

# launch a Kafka consumer
bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 \
--topic favorite-color-output \
--from-beginning \
--formatter kafka.tools.DefaultMessageFormatter \
--property print.key=true \
--property print.value=true \
--property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer \
--property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer

# launch the streams application

# then produce data to it
bin/kafka-console-producer.sh --bootstrap-server localhost:9092 --topic favorite-color-input

#
stephane,blue
john,green
stephane,red
alice,red

