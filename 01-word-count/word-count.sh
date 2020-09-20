#!//bin/bash

# create input topic
bin/kafka-topics.sh --create --topic word-count-input \
--bootstrap-server localhost:9092 \
--replication-factor 1 --partitions 3

# create output topic
bin/kafka-topics.sh --create --topic word-count-output  \
--bootstrap-server localhost:9092 \
--replication-factor 1 --partitions 3

# list topics
bin/kafka-topics.sh --list --bootstrap-server localhost:9092

# create consumer
bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 \
--topic word-count-output \
--from-beginning \
--formatter kafka.tools.DefaultMessageFormatter \
--property print.key=true \
--property print.value=true \
--property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer \
--property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer

# create producer
bin/kafka-console-producer.sh --topic word-count-input --bootstrap-server localhost:9092


# show changelog
bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 \
--topic word-count-app-KSTREAM-AGGREGATE-STATE-STORE-0000000004-changelog \
--from-beginning \
--formatter kafka.tools.DefaultMessageFormatter \
--property print.key=true \
--property print.value=true \
--property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer \
--property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer

# show repartition
bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 \
--topic word-count-app-KSTREAM-AGGREGATE-STATE-STORE-0000000004-repartition \
--from-beginning \
--formatter kafka.tools.DefaultMessageFormatter \
--property print.key=true \
--property print.value=true \
--property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer \
--property value.deserializer=org.apache.kafka.common.serialization.StringDeserializer
