#!//bin/bash

bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 3 --topic word-count-input

bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 3 --topic word-count-output

bin/kafka-topics.sh --list --bootstrap-server localhost:9092

