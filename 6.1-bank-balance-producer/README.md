# Producer

## Configurations
- ```acks```: (strongest producing guarantee) that makes sure your message have been written into all broker servers (replicas),
so your message won't be lost.

- ```retries```: retries to write messages, that would make messages not lost.
  
- ```linger.ms```: (not for production) make producer send sync quickly.

- ```enable.idempotence```: When set to 'true', the producer will ensure that exactly one copy of each message is written in the stream.
If 'false', producer retries due to broker failures, etc., may write duplicates of the retried message in the stream. 
Note that enabling idempotence requires max.in.flight.requests.per.connection to be less than or equal to 5, retries to be greater than 0 and acks must be 'all'.
