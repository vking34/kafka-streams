# Kafka Streams

## Definition
- Client library
- Easy data processing and transformation library set on Kafka.

## Usages
We use it to process and analyze data stored in Kafka. It relied on important streams processing concepts like properly distinguishing between event time and processing time, windowing support, and simple yet efficient management and real-time querying of application state.

- Data Transformations
- Data Enrichment
- Fraud Detection
- Monitoring and Altering

## Key Points
- A simple and lightweight client library. Kafka Stream can be easily embedded in applications in multiple languages, specially Java.
- No external dependencies on systems other than Apache Kafka itself as the internal messaging layer.
- Enable very fast and efficient stateful operations (windowed joins and aggregations), it supports the fault-tolerant local state.
- To guarantee that each record will be processed once and only once even when there is a failure on either Streams clients or Kafka brokers in the middle of processing, it offers exactly-once processing semantics.
- Highly scalable, elastic.
- In order to achieve millisecond processing latency, employs one-record-at-a-time processing. Also, with the late arrival of records, it supports event-time based windowing operations.

#
### Follow to the directories in order to learn concepts step by step. 

#
## References:
1. https://www.udemy.com/course/kafka-streams/
2. https://data-flair.training/blogs/kafka-streams/