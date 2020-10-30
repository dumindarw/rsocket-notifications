## Send / Receive Notifications with RSocket

RSocket will ease the communication between microservices and a perfect replacement for traditional http communication.

1. Microservices send notifications to central RSocket Server.
2. Notification service pull the content from RSocket Server on Client Request.
3. WebSocket Client will receive notifications realtime if subscribed to the notification service.

