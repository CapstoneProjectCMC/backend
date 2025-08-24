package com.codecampus.submission.configuration.redis;

import dtos.UserSummary;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
  @Bean
  public RedisConnectionFactory redisConnectionFactory(
      @Value("${spring.data.redis.host}") String host,
      @Value("${spring.data.redis.port}") int port,
      @Value("${spring.data.redis.password:}") String password) {

    LettuceConnectionFactory cf =
        new LettuceConnectionFactory(host, port);
    cf.setPassword(password);
    return cf;
  }

  @Bean
  public RedisTemplate<String, UserSummary> userSummaryRedisTemplate(
      RedisConnectionFactory cf) {
    RedisTemplate<String, UserSummary> tpl = new RedisTemplate<>();
    tpl.setConnectionFactory(cf);

    StringRedisSerializer keySer = new StringRedisSerializer();
    GenericJackson2JsonRedisSerializer valSer =
        new GenericJackson2JsonRedisSerializer();

    tpl.setKeySerializer(keySer);
    tpl.setHashKeySerializer(keySer);
    tpl.setValueSerializer(valSer);
    tpl.setHashValueSerializer(valSer);

    tpl.afterPropertiesSet();
    return tpl;
  }

  @Bean(destroyMethod = "shutdown")
  public RedissonClient redissonClient(
      @Value("${spring.data.redis.host}") String host,
      @Value("${spring.data.redis.port}") int port,
      @Value("${spring.data.redis.password:}") String password) {
    Config config = new Config();
    config.useSingleServer()
        .setAddress("redis://" + host + ":" + port)
        .setPassword(password.isBlank() ? null : password)
        .setConnectionPoolSize(16)
        .setConnectionMinimumIdleSize(4);
    return Redisson.create(config);
  }
}
