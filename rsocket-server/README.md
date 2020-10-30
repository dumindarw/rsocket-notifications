#RSocket Server Implementation

Async message passer over single connection.It supports session resumption, to allow resuming long-lived streams across different transport connections. 
This is particularly useful for mobile⬄server communication when network connections drop, switch, and reconnect frequently.

https://rsocket.io/docs/Motivations

## Redis

Redis required to be up and running prior to the startup

```bash
sudo docker run --name omasaga-redis -d -p 6379:6379 redis:6.0.5
```
