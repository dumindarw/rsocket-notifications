package lk.drw.rsocket.server.config;

import java.time.Duration;

import org.springframework.boot.rsocket.server.RSocketServerCustomizer;
import org.springframework.stereotype.Component;

import io.rsocket.core.RSocketServer;
import io.rsocket.core.Resume;
import lombok.extern.slf4j.Slf4j;
import reactor.util.retry.Retry;

/**
 * Resuming server after disconnect
 *
 * @author Duminda Wanninayake
 * @since 22.07.2020
 */

@Component
@Slf4j
public class RSocketServerResumptionConfig implements RSocketServerCustomizer {

  /**
   * Customize rSocketServer resume capabilities
   *
   * @param rSocketServer instance
   */

  @Override
  public void customize(RSocketServer rSocketServer) {
    log.info("customize rSocketServer");
    rSocketServer.resume(new Resume()
        .streamTimeout(Duration.ofSeconds(1))
        .sessionDuration(Duration.ofMinutes(15))
        .retry(
            Retry.fixedDelay(Long.MAX_VALUE, Duration.ofSeconds(2))
                .doBeforeRetry(s -> log.debug("Disconnected. Trying to resume..."))));
  }

}