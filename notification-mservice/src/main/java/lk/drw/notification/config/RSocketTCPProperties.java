package lk.drw.notification.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * Retreive RSocket connection details from yml file
 *
 * @author Duminda Wanninayake
 *
 */
@ConfigurationProperties("rsockettcp")
@Data
@Component
public class RSocketTCPProperties {

  private String host;

  private int port;

}