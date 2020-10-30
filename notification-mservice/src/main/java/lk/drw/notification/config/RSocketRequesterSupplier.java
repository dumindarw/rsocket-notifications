package lk.drw.notification.config;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import org.springframework.stereotype.Component;

import io.rsocket.SocketAcceptor;
import io.rsocket.core.Resume;
import io.rsocket.exceptions.RejectedResumeException;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;

/**
 *  RSocketRequester with Resume support
 *
 * @author  Duminda Wanninayake
 * @since 2020.07.23
 *
 */

@Component
@Slf4j
public class RSocketRequesterSupplier implements Supplier<RSocketRequester> {

  private static final AtomicReference<RSocketRequester> R_SOCKET_REQUESTER =
      new AtomicReference<>();

  @Autowired
  private RSocketRequester.Builder rSocketRequesterBuilder;

  @Autowired
  RSocketTCPProperties rsocketProperties;

  private final RSocketStrategies rsocketStrategies;

  @Autowired
  RSocketRequesterSupplier(RSocketStrategies rsocketStrategies) {
    this.rsocketStrategies = rsocketStrategies;
  }

  @PostConstruct
  void init() {
    final SocketAcceptor responder = RSocketMessageHandler.responder(rsocketStrategies, new ClientHandler());
    rSocketRequesterBuilder
        .rsocketConnector(configurer -> {
          configurer.acceptor(responder);
          configurer.resume(
              new Resume()
                  .streamTimeout(Duration.ofSeconds(1))
                  .sessionDuration(Duration.ofMinutes(10))
                  .retry(
                      Retry.fixedDelay(Long.MAX_VALUE, Duration.ofSeconds(2))
                          .doBeforeRetry(s -> log.info("Disconnected. Trying to resume..."))
                          .doAfterRetry(c -> init())));
        })

        .connectTcp(rsocketProperties.getHost(), rsocketProperties.getPort())
        .doOnError(throwable -> {
          log.error("doOnError");
          if (throwable instanceof RejectedResumeException) {
            init();
          }
        })
        .subscribe(R_SOCKET_REQUESTER::set);
  }

  @Override
  public RSocketRequester get() {
    return R_SOCKET_REQUESTER.get();
  }
}

/**
 *  Custom client handler will send back connection status
 *
 * @author  Duminda Wanninayake
 * @since 2020.07.23
 *
 */
@Slf4j
class ClientHandler {

  @MessageMapping("client-status")
  public Flux<String> statusUpdate(String status) {
    log.info("Connection {}", status);
    return Flux.interval(Duration.ofSeconds(5)).map(index -> String.valueOf(Runtime.getRuntime().freeMemory()));
  }
}
