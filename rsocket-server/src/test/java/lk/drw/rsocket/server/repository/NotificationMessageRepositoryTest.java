package lk.drw.rsocket.server.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import lk.drw.rsocket.server.model.Notification;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SuppressWarnings("rawtypes")
@SpringBootTest
@Testcontainers
class NotificationMessageRepositoryTest {

  @Rule
  public static GenericContainer redis = new GenericContainer("redis:6.0.5")
      .withExposedPorts(6379);

  @Autowired
  private NotificationMessageRepository notificationRepository;

  @BeforeAll
  public static void startContainerAndPublicPortIsAvailable() {
    redis.start();
  }

  @Test
  void test_redis_running() {
    assertTrue(redis.isRunning());
  }

  @Test
  void test_notification_save() {

    final Notification n = new Notification(1, "New Message", "sendUser", "receiver", 1595499410);

    final Mono<Long> notification = notificationRepository
        .saveNotification(n);

    StepVerifier.create(notification.log()).consumeNextWith(message -> {
      assertThat(message).isGreaterThan(Long.valueOf(0));
    }).verifyComplete();

  }

  @Test
  void test_notification_retreival() {
    final Flux<Notification> notifications = notificationRepository.findNotificationsByReceiver("receiver");

    final Notification n = new Notification(1, "New Message", "sendUser", "receiver", 1595499410);

    StepVerifier.create(notifications.log())
        .expectNext(n)
        .expectComplete();
  }

}
