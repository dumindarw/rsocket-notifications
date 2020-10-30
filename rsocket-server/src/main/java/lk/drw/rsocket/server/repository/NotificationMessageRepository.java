package lk.drw.rsocket.server.repository;

import lk.drw.rsocket.server.model.Notification;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface NotificationMessageRepository {

  /**
   * Save incoming notification inside Redis Store
   * @param message notification input
   * @return last inserted id
   */
  public Mono<Long> saveNotification(Notification message);

  /**
   * Retrieve all messages by receiver from Redis
   * @param receiver , who needs notifications
   * @return List of Notifications
   */
  public Flux<Notification> findNotificationsByReceiver(String receiver);

}
