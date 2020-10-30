package lk.drw.rsocket.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import lk.drw.rsocket.server.model.Notification;

/**
 * Redis Custom Json Serializer and Template initializer
 *
 * @author  Rajitha Wanninayake
 * @since 22.07.2020
 */

@Configuration
public class RedisConfiguration {

  @Bean
  ReactiveRedisOperations<String, Notification> reactiveRedisOperations(ReactiveRedisConnectionFactory factory) {
    final Jackson2JsonRedisSerializer<Notification> serializer = new Jackson2JsonRedisSerializer<>(Notification.class);

    final RedisSerializationContext.RedisSerializationContextBuilder<String, Notification> builder =
        RedisSerializationContext.newSerializationContext(new StringRedisSerializer());

    final RedisSerializationContext<String, Notification> context = builder.value(serializer).build();

    return new ReactiveRedisTemplate<>(factory, context);
  }

}
