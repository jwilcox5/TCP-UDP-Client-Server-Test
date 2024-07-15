- This Java project creates two TCP clients, two TCP servers, two UDP clients, and two UDP servers and performs the following test on them:

1. Measure round-trip latency (RTTs) and how it varies with message size in TCP, by sending and receiving (echoing and validating) messages of size 8, 64, and 512 bytes.
2. The same as (1), except using UDP.
3. Measure throughput (bits per second) and how it varies with message size in TCP, by sending 1MByte of data (with a 8-byte acknowledgment in the reverse direction) using different numbers of messages: 16384 64Byte messages, vs 4096 256Byte messages, vs 1024 1024Byte messages. Use known message contents (for example number sequences) so they can be validated.
4. The same as (3), using UDP.

- A simple XOR cipher was used to encrypt/decrypt all messages that were sent between the client and the server and all the tests were performed across 3 pairs of machines across 2 different networks. The results of the test can be found in the results folder.
