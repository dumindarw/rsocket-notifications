package lk.drw.notification.controller;

import java.time.Instant;

import javax.validation.constraints.Pattern;

import lk.drw.notification.model.Notification;
import lk.drw.notification.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
@Validated
public class NotificationController {

  @Autowired
  NotificationService notificationService;

  /**
   * User facing notification end-point (through web-sockets)
   *
   * @author  Duminda Wanninayake
   * @since 22.07.2020
   */
  @MessageMapping("notification.message")
  public Flux<Notification> fileNotification() {
    log.info("fileNotification Server file.notification.message");

    return notificationService.findMessage().onErrorResume(e -> {
      log.error(e.getMessage());
      return Flux.just(new Notification(0, e.getMessage(), "", "", Instant.now().getEpochSecond()));
    }).log();

  }

  /**
   * Route login request from client to server
   *
   * @param username is the logging username
   * @return return back user
   */
  @MessageMapping("login")
  Mono<String> signin(
      @Pattern(regexp = "^[\"a-zA-Z]{7,128}$", message = "Alphabetic username should contains 5 to 128 characters") String username) {

    log.info("signin input: {}", username);

    return notificationService.login(username)
        .onErrorResume(e -> {
          log.error(e.getMessage());
          return Mono.just(e.getMessage());
        })
        .log();

  }

  /**
   * Route logout request from client to server
   *
   * @param username is the logging username
   * @return return back user
   */

  @MessageMapping("logout")
  Mono<String> logout(
      @Pattern(regexp = "^[\"a-zA-Z]{7,128}$", message = "Alphabetic username should contains 5 to 128 characters") String username) {

    log.info("logout input: {}", username);

    return notificationService.logout(username)
        .onErrorReturn(
            "Logging out ...")
        .log();

  }

  /**
   * Route for old message request
   *
   * @return Stream of Notifications
   */
  @MessageMapping("notification.message.{receiver}")
  public Flux<Notification> notification(
      @DestinationVariable @Pattern(regexp = "^[\"a-zA-Z]{7,128}$", message = "Alphabetic receiver should contains 5 to 128 characters") String receiver) {
    log.info("Notification Server notification.message " + receiver);

    return notificationService.findMessagesByReceiver(receiver)
        .onErrorResume(e -> {
          log.error(e.getMessage());
          return Flux.just(new Notification(0, e.getMessage(), "", receiver, Instant.now().getEpochSecond()));
        })
        .log();

  }

}
