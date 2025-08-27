package com.codecampus.profile.config.redis;

import dtos.ContestStatusDto;
import dtos.ContestSummary;
import dtos.ExerciseStatusDto;
import dtos.ExerciseSummary;
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
    LettuceConnectionFactory cf = new LettuceConnectionFactory(host, port);
    cf.setPassword(password);
    return cf;
  }

  @Bean
  public RedisTemplate<String, ExerciseSummary> exerciseRedisTemplate(
      RedisConnectionFactory cf) {
    RedisTemplate<String, ExerciseSummary> tpl = new RedisTemplate<>();
    tpl.setConnectionFactory(cf);
    var keySer = new StringRedisSerializer();
    var valSer = new GenericJackson2JsonRedisSerializer();
    tpl.setKeySerializer(keySer);
    tpl.setValueSerializer(valSer);
    tpl.setHashKeySerializer(keySer);
    tpl.setHashValueSerializer(valSer);
    tpl.afterPropertiesSet();
    return tpl;
  }

  @Bean
  public RedisTemplate<String, ExerciseStatusDto> exerciseStatusRedisTemplate(
      RedisConnectionFactory cf) {
    RedisTemplate<String, ExerciseStatusDto> tpl = new RedisTemplate<>();
    var keySer = new StringRedisSerializer();
    var valSer = new GenericJackson2JsonRedisSerializer();
    tpl.setConnectionFactory(cf);
    tpl.setKeySerializer(keySer);
    tpl.setHashKeySerializer(keySer);
    tpl.setValueSerializer(valSer);
    tpl.setHashValueSerializer(valSer);
    tpl.afterPropertiesSet();
    return tpl;
  }

  @Bean
  public RedisTemplate<String, ContestSummary> contestRedisTemplate(
      RedisConnectionFactory cf) {
    RedisTemplate<String, ContestSummary> tpl = new RedisTemplate<>();
    tpl.setConnectionFactory(cf);
    var keySer = new StringRedisSerializer();
    var valSer = new GenericJackson2JsonRedisSerializer();
    tpl.setKeySerializer(keySer);
    tpl.setValueSerializer(valSer);
    tpl.setHashKeySerializer(keySer);
    tpl.setHashValueSerializer(valSer);
    tpl.afterPropertiesSet();
    return tpl;
  }

  @Bean
  public RedisTemplate<String, ContestStatusDto> contestStatusRedisTemplate(
      RedisConnectionFactory cf) {
    RedisTemplate<String, ContestStatusDto> tpl = new RedisTemplate<>();
    var keySer = new StringRedisSerializer();
    var valSer = new GenericJackson2JsonRedisSerializer();
    tpl.setConnectionFactory(cf);
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
        .setPassword((password == null || password.isBlank()) ? null : password)
        .setConnectionPoolSize(16)
        .setConnectionMinimumIdleSize(4);
    return Redisson.create(config);
  }
}
