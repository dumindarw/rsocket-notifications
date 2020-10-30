package lk.drw.rsocket.server;

import static org.assertj.core.api.Assertions.assertThat;

import lk.drw.rsocket.server.model.Notification;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.rsocket.context.LocalRSocketServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.rsocket.RSocketRequester;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
class RSocketClientToServerTest {

  private static RSocketRequester requester;

  @BeforeAll
  public static void setupOnce(@Autowired RSocketRequester.Builder builder,
      @LocalRSocketServerPort Integer port) {

    requester = builder
        .connectTcp("localhost", 7000)
        .block();
  }

  @Test
  void testRequestGetsResponse_notification_message_in() {

    final Mono<Long> result = requester
        .route("notification.message.in")
        .data(new Notification(2, "New Message 2", "sendUser2", "receiver2", 1595499416))
        .retrieveMono(Long.class);

    StepVerifier
        .create(result)
        .consumeNextWith(message -> {
          assertThat(message).isGreaterThan(Long.valueOf(0));
        })
        .verifyComplete();
  }

  @Test
  void testRequestGetsResponse_notification_messages_by_receiver() {
    final Flux<Notification> result = requester
        .route("notification.message.{receiver}", "receiver2")
        .retrieveFlux(Notification.class);

    StepVerifier.create(result.log())
        .expectNext(new Notification(2, "New Message 2", "sendUser2", "receiver2", 1595499416))
        .expectComplete();
  }

  @AfterAll
  public static void tearDownOnce() {
    requester.rsocket().dispose();
  }

}
