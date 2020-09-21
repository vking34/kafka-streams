# KStreams and KTables

## KStreams
1. Definition:
    - __An abstraction of a record stream__
    - All inserts
    - Similar to a log
    - Infinite
    - Unbounded data streams
    
2. When to use:
    - Reading from a topic that's not compacted
    - If new data is partial information / transactional

## KTable
1. Definition:
    - An abstraction of a changelog stream
    - All upserts __(update) on non null values__
    - __Deletes on null values__
    - Similar to a database table
    - Parallel with log compacted

2. When to use:
    - Reading from a topic that's log-compacted (aggregations)
    - If you need a structure that's like a database table, where every update is self sufficient.
    
## GlobalKTable
1. Definition:
    - An abstraction of a changelog stream

2. Differences between KTable and GlobalKTable
    - If you read the input topic into a KTable , then the "local" KTable instance of each application instance will be populated with data from only 1 partition of the topic's 5 partitions.
    - If you read the input topic into a GlobalKTable , then the local GlobalKTable instance of each application instance will be populated with data from all partitions of the topic.
    
3. Benefits
    - More convenient and/or efficient joins
    - Can be used to "broadcast" information to all the running instances of your application.
    
4. Downsides of global tables:
    - Increased local storage consumption compared to the (partitioned) KTable because the entire topic is tracked.
    - Increased network and Kafka broker load compared to the (partitioned) KTable because the entire topic is read.

#
## Duality

__Stream as Table__: a stream can be considered a changelog of table, where each data record in the stream captuers a state change of the table.

__Table as Stream__: a table can be considered a snapshot, at a point in time, of the lastest value for each key in a stream.

![](https://cdn.confluent.io/wp-content/uploads/changelog.gif)

1. Transforming KTable to KStream
```
KStream<String, String> stream = table.toStream();
```

2. Transforming KStream to KTable
    1. Chain a groupByKey() and aggregation functions (count, aggregate, reduce)
    ```
    KTable<String, Long> table = colorStream.groupByKey().count();
    ```
    2. Write back to Kafka and read as KTable
    ```
    // write to kafka
    stream.to("imtermediary-topic");
    
    // read from kafka as table
    KStream<String, Long> table = builder.table("imtermediary-topic")
    ```

#
## Statleless & Stateful
1. __Stateless__: the result of transformation only depends on the data-point you process.
    - Example: multiply value by 2
        - 1 -> 2
        - 300 -> 600
2. __Stateful__: the result of a transformation also depends on an external information - the __state__.
    - Example: Count operation
        - hello -> 1
        - hello -> 2


#
## Stateless Operations
1. Map / MapValues
    - Takes 1 record and produces 1 (modified) record
    - MapValues:
        - Only affecting values
        - -> does not change keys -> does not trigger a repartition
        - For KStreams and KTables
        
    - Map:
        - Affects both keys and values
        - Triggers a re-partitions
        - For KStream only
    
2. Filter / FilterNot
    - Take 1 record and produces 0 or 1 record
    - Filter:
        - Does not change keys / values
        - -> Does not trigger a repartition
        - For KStreams and Ktables
        
    - FilterNot:
        - Inverse of filter
        
3. FlatMapValues / FlatMap
    - Take 1 record and produce 0 or 1 or more record
    - FlatMapValues:
        - Does not change keys -> does not trigger a repartition
        - For KStream only
        
    - FlatMap:
        - Change keys -> triggers a repartition
        - For KStreams only
        
4. Branch
    - Branch (split) a KStream into 1 or more KStream.
    - Predicates are evaluated in order, if nore matches, records are dropped
    - Example:
    ```
    KStream<String, Long>[] branches = stream.branch(
    (key, value) -> value > 100, 
    (key, value) -> value > 10, 
    (key, value) -> value > 0 
   );
    ```
   
   
5. SelectKey
    - Assigns a new Key to record (from old key and value)
    - Trigger (mark) the data for repartitioning
    - Best practice to isolate that transformation to know exactly where the partition happens
    
    