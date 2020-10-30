package lk.drw.notification.config;

import java.util.Map;

import org.springframework.boot.rsocket.messaging.RSocketStrategiesCustomizer;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeType;

/**
 * Creating custom Mime type for RSocket communications
 *
 * @author Duminda Wanninayake
 * @since 22.07.2020
 */
@Component
public class JsonMetadataStrategiesCustomizer implements RSocketStrategiesCustomizer {

  public static final MimeType METADATA_MIME_TYPE = MimeType.valueOf("application/vnd.spring.rsocket.metadata+json");

  private static final ParameterizedTypeReference<Map<String, String>> METADATA_TYPE =
      new ParameterizedTypeReference<Map<String, String>>() {
      };

  /**
   * Override customize method in order to inject custom mime type
   */
  @Override
  public void customize(RSocketStrategies.Builder strategies) {
    strategies
        .metadataExtractorRegistry(registry -> registry.metadataToExtract(METADATA_MIME_TYPE, METADATA_TYPE,
            (in, map) -> map.putAll(in)));
  }

}