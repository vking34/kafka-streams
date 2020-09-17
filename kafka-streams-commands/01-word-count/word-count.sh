#!//bin/bash

bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 3 --topic word-count-input

bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 3 --topic word-count-output

bin/kafka-topics.sh --list --bootstrap-server localhost:9092

bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 \
    --topic word-count-output \
    --from-beginning \
    --formatter kafka.tools.DefaultMessageFormatter \
    --property print.key=true \
    --property print.value=true \
    --property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer \
    --property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer


bin/kafka-console-producer.sh --bootstrap-server localhost:9092 --topic word-count-input

bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 \
    --topic word-count-app-KSTREAM-AGGREGATE-STATE-STORE-0000000004-changelog \
    --from-beginning \
    --formatter kafka.tools.DefaultMessageFormatter \
    --property print.key=true \
    --property print.value=true \
    --property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer \
    --property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer


bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 \
    --topic word-count-app-KSTREAM-AGGREGATE-STATE-STORE-0000000004-repartition \
    --from-beginning \
    --formatter kafka.tools.DefaultMessageFormatter \
    --property print.key=true \
    --property print.value=true \
    --property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer \
    --property value.deserializer=org.apache.kafka.common.serialization.StringDeserializer
