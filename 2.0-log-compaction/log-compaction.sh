#!/bin/bash


# create compacted topic
bin/kafka-topics.sh --create \
--topic employee-salary-compact \
--bootstrap-server localhost:9092 \
--replication-factor 1 --partitions 1 \
--config cleanup.policy=compact \
--config min.cleanable.dirty.ratio=0.005 \
--config segment.ms=10000


# create consumer
bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 \
    --topic employee-salary-compact \
    --from-beginning \
    --property print.key=true \
    --property key.separator=,


# create producer
bin/kafka-console-producer.sh --bootstrap-server localhost:9092 \
--topic employee-salary-compact \
--property parse.key=true \
--property key.separator=,

# produce message
123,{"John": "8000"}
456,{"Mark": "9000"}
789,{"Lisa": "9500"}

# check consumer
777,{"Travis": "9500"}
123,{"John": "10000"}
789,{"Lisa": "12000"}

# restart consumer and see the magic
999,{"Phiplip": "12500"}
