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

