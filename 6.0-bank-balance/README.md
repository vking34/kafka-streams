# Bank Balance App

## Notes
- The order arguments in "adder" lambda function:
    1. key
    2. new value
    3. current state

- We must have ```KeyValueStore<Bytes, byte[]>``` in ```Materialized```.

- ```cache.max.bytes.buffering```: ```"0"``` the message data will be committed as soon as it happens. (not for production)
