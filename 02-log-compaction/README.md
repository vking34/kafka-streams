# Log Compaction

## Definition
Log compaction __retains at least the last known value for each record key__ for a single topic partition. Compacted logs are useful for restoring state after a crash or system failure.


A log compacted topic log contains a full snapshot of final record values for every record key not just the recently changed keys.

## Notes

- Log Compaction has to be enable by you on the topic creation.

- Ordering of messages it kept, log compaction only removes some messages, but does not re-order them. That means offset is immutable.

- Deleted records can still be seen by consumers for a period of detele.retentions.ms (default 24 hours)

- It does not prevent you from pushing duplicate data to Kafa
    - De-duplication is done after a segment is committed
    - Your consumers will still read from head as soon as the data arrives

- Log compaction can fail from time to time
    - It is optimization and it the compaction thread might crash
    - Make sure you assign enough memory to it and that it gets triggered



## Usages
- Log Compaction can be a huge improvement in performance when dealing with KTables because eventually records get discarded.
