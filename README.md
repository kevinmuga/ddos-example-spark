# ddos-example-spark
DDOS attack detector.

Overview: A website is periodically attacked by a botnet in a Distribute Denial of Service attack. This application uses a log file in apache log format from the attack to detect DDOS attacks in real time.

- Ingest: Reading a file from local disk and writing to Kafka. 
- Detection: Reads messages from the message system and detects whether the attacker is part of the DDOS attack. 
             Once attacker is detected, the ip-address is written to a results directory to be used for further processing. 
             The results are also displayed in HTML format.
