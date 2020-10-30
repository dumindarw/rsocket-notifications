package lk.drw.notification;

import java.net.URI;

import lk.drw.notification.model.Notification;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.messaging.rsocket.RSocketRequester;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RSocketClientToServerTest {

  @Autowired
  private RSocketRequester.Builder requesterBuilder;

  Mono<RSocketRequester> requester;

  private URI uri;

  @Test
  void testRequestGetsResponse_notification_messages_by_receiver() {

    requester = this.requesterBuilder
        .dataMimeType(MediaType.APPLICATION_CBOR)
        .connectWebSocket(this.uri);

    final Flux<Notification> result = requester.flatMapMany(transformer -> transformer.route("notification.message.{receiver}", "receiver")
        .retrieveFlux(Notification.class));

    StepVerifier.create(result.log())
        .expectNextCount(0)
        .expectComplete();
  }

  @LocalServerPort
  public void setPort(int port) {
    this.uri = URI.create("ws://localhost:" + port + "/rsocket");
  }

}
