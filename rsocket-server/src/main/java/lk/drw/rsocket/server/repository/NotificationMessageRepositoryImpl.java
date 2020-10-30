package lk.drw.rsocket.server.repository;

import lk.drw.rsocket.server.model.Notification;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class NotificationMessageRepositoryImpl implements NotificationMessageRepository {

  private final ReactiveRedisOperations<String, Notification> messageOps;

  public NotificationMessageRepositoryImpl(
      ReactiveRedisOperations<String, Notification> ops) {
    this.messageOps = ops;
  }

  /**
   * Save incoming notification inside Redis Store as a list item
   * @param message
   * @return Mono Long
   */
  @Override
  public Mono<Long> saveNotification(Notification message) {

    return messageOps.opsForList().leftPush(message.getReceiver(), message);
  }

  /**
   * Retrieve all messages by receiver from Redis List
   * @param receiver
   * @return Flux List Notification
   */
  @Override
  public Flux<Notification> findNotificationsByReceiver(String receiver) {
    return messageOps.opsForList().range(receiver, 0, -1);
  }

}
