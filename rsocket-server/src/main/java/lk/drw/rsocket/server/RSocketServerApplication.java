package lk.drw.rsocket.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class RSocketServerApplication {

  public static void main(String[] args) {
    SpringApplication.run(RSocketServerApplication.class, args);
  }

}