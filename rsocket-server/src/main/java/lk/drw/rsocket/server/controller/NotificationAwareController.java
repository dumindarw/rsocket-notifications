package lk.drw.rsocket.server.controller;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import lk.drw.rsocket.server.model.Notification;
import lk.drw.rsocket.server.repository.NotificationMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Controller;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxProcessor;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

/**
 * Communicating controller between Clients
 *
 * @author  Duminda Wanninayake
 * @since 22.07.2020
 */

@Controller
@Slf4j
public class NotificationAwareController {

  private static final Map<String, RSocketRequester> REQUESTER_MAP = new HashMap<>();

  @Autowired
  NotificationMessageRepository notificationRepositry;

  private final FluxProcessor<Notification, Notification> processor = DirectProcessor.<Notification> create().serialize();

  private final FluxSink<Notification> sink = processor.sink();

  /**
   * User connect endpoint, send status to client
   *
   * @param rSocketRequester is the rsocket tcp server connection requester
   * @param userId is the logged in username
   */
  @MessageMapping("user-connect")
  public void onConnect(RSocketRequester rSocketRequester, @Payload String userId) {
    log.info("Client is connecting ... {} " + userId);

    REQUESTER_MAP.put(userId, rSocketRequester);

    rSocketRequester.route("client-status")
        .data("OPEN")
        .retrieveFlux(String.class)
        .doOnNext(s -> log.info("Client: {} Free Memory: {}.", userId, s))
        .subscribe();

  }

  /**
   * User disconnect endpoint
   *
   * @param rSocketRequester is the rsocket tcp server connection requester
   * @param userId is the logged in username
   */
  @MessageMapping("user-disconnect")
  public void onDisconnect(RSocketRequester rSocketRequester, @Payload String userId) {
    log.info("user logout request " + userId);

    rSocketRequester.rsocket().onClose().subscribe(null, null, () -> REQUESTER_MAP.remove(userId));

    if (!rSocketRequester.rsocket().isDisposed()) {
      rSocketRequester.rsocket().dispose();
    }

  }

  /**
   * Listen to the micro-service messages (Request-Response)
   *
   * @param message is the microservice message
   * @return last insertion id
   */
  @MessageMapping("notification.message.in")
  Mono<Long> request(Notification message) {
    log.info("request notification.message.in {} " + message);

    sink.next(message);

    return notificationRepositry.saveNotification(message);
  }

  /**
   * Passing incoming notification to receiver (Request-Stream)
   *
   * @return send stream of messages back to notification service
   */
  @MessageMapping("notification.message.out")
  public Flux<Notification> returnMessages() {
    log.info("response file.notification.message.out ");

    return Flux
        .merge(
            Flux.empty(),
            processor)
        .delayElements(Duration.ofSeconds(1));

  }

  /**
   * Respond to the clients,  send notification history
   *
   * @param receiver is the notification requester
   * @return send old message list back
   */
  @MessageMapping("notification.message.{receiver}")
  public Flux<Notification> returnOldMessages(@DestinationVariable String receiver) {
    log.info("response notification.message.out ");

    return notificationRepositry.findNotificationsByReceiver(receiver);

  }

}
