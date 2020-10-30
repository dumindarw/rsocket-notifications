package lk.drw.notification.service;

import java.time.Duration;

import lk.drw.notification.config.RSocketRequesterSupplier;
import lk.drw.notification.model.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 *
 * Service to communicate with RSocker server
 *
 * @author Duminda Rajitha Wanninayake
 * @since 22.07.2020
 */

@Component
@Slf4j
public class NotificationService {

  @Autowired
  private RSocketRequesterSupplier rSocketRequesterSupplier;

  /**
   * Serve login request from client to server
   *
   * @param user is the logging username
   * @return return back user
   */
  public Mono<String> login(String user) {
    log.info("User Login request : " + user);

    this.rSocketRequesterSupplier.get()
        .route("user-connect")
        .data(user)
        .send()
        .doOnError(error -> log.warn("Connection CLOSED"))
        .doFinally(consumer -> log.info("Client DISCONNECTED"))
        .log()
        .subscribe();

    return Mono.just(user);

  }

  /**
   * Serve logout request from client to server
   *
   * @param user is the logging username
   * @return return back user
   */
  public Mono<String> logout(String user) {
    log.info("User Logout request : " + user);

    this.rSocketRequesterSupplier.get()
        .route("user-disconnect")
        .data(user)
        .send()
        .doOnError(error -> log.warn("Connection CLOSED"))
        .doFinally(consumer -> log.info("Client DISCONNECTED"))
        .log()
        .subscribe();

    return Mono.just(user);

  }

  /**
   * Serve message request from client to server
   *
   * @return Stream of Notifications
   */
  public Flux<Notification> findMessage() {
    log.info("Request notification.message data from RSocket TCP server ");

    return this.rSocketRequesterSupplier.get()
        .route("notification.message.out")
        .retrieveFlux(Notification.class);

  }

  /**
   * Serve old message request from client to server
   *
   * @return  Stream of old Notifications
   */
  public Flux<Notification> findMessagesByReceiver(String receiver) {
    log.info("Request old notification.message data from RSocket TCP server " + receiver);
    return this.rSocketRequesterSupplier.get()
        .route("notification.message.{receiver}", receiver)
        .retrieveFlux(Notification.class);
  }
}

@Slf4j
class ClientHandler {

  /**
   * Handler to get client status
   *
   * @param  status is the connection status
   * @return  Stream of client status as a freememory
   */
  @MessageMapping("client-status")
  public Flux<String> statusUpdate(String status) {
    log.info("Connection {}", status);
    return Flux.interval(Duration.ofSeconds(5)).map(index -> String.valueOf(Runtime.getRuntime().freeMemory()));
  }
}