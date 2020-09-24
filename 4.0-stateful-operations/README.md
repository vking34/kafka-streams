# Relationship between abstract models and operations
![](refs/img/streams-stateful_operations.png)


#
# Stateful Transformations
## groupBy
- it triggers a repartition because the key may change

```
KGroupedTable<String, Integer> groupedTable = table.groupBy(
    (key, value) -> KeyValue.pair(value, value.length()),
    Grouped.with(
      Serdes.String(), /* key (note: type was modified) */
      Serdes.Integer()) /* value (note: type was modified) */
);
```

## Count
- groupBy/groupByKey returns KGroupStream/KGroupTable

- counts the number of records by grouped key

- If used on KGroupStream:
    - Null keys or null values are ignored

- If used on KGroupTable:
    - Null keys are ignored
    - Null values are treated as ```delete```

## Aggregate

- KGroupStream:
    - Need arguments:
        - initializer (of any type)
        - a "adder" aggregation function
        - a State Store name
        - a value Serdes

    ```
    KTable<byte[], Long> aggregatedStream = groupedStream.aggregate(
        () -> 0L, /* initializer */
        (aggKey, newValue, aggValue) -> aggValue + newValue.length(), /* adder */
        Materialized.as("aggregated-stream-store") /* state store name */
            .withValueSerde(Serdes.Long()) /* serde for aggregate value */ 
    );
    ```

- KGroupTable
    - Need arguments:
        - initializer (of any type)
        - a "adder" aggregation function
        - a "subtractor" aggregation function. Data from a table can be deleted. And for one data is deleted, we need to know what to do to aggregator when losing data.
        - a State Store name
        - a value Serdes

    ```
    KTable<byte[], Long> aggregatedTable = groupedTable.aggregate(
        () -> 0L, /* initializer */
        (aggKey, newValue, aggValue) -> aggValue + newValue.length(), /* adder */
        (aggKey, oldValue, aggValue) -> aggValue - oldValue.length(), /* subtractor */
        Materialized.as("aggregated-table-store") /* state store name */
        .withValueSerde(Serdes.Long()) /* serde for aggregate value */
    );
    ```


## Reduce
- Similar to ```aggregate```, but the result type has to be the same as an input.


```
// Reducing a KGroupedStream
KTable<String, Long> aggregatedStream = groupedStream.reduce(
    (aggValue, newValue) -> aggValue + newValue /* adder */);

// Reducing a KGroupedTable
KTable<String, Long> aggregatedTable = groupedTable.reduce(
    (aggValue, newValue) -> aggValue + newValue, /* adder */
    (aggValue, oldValue) -> aggValue - oldValue /* subtractor */);

```

## Peek
- ```peek``` allows you to apply a side-effect operation to KStream and get the same KStream as result. __It does not modify KStream.__

- A side effect could be:
    - Printing the stream to the console
    - Statistic collection

- Warning: it could be __executed multiple times__ because it is side effect.

```
KStream<byte[], String> unmodifiedStream = stream.peek(
    (key, value) -> System.out.println("key=" + key + ", value=" + value));
```


## Notes:
- It it not recommend to write the result to an external system, although it is theoretically doable to do it using Kafka streams.

- The recommend way is using __Kafka Connect__




