package com.codecampus.coding.config.redis;

import com.codecampus.coding.grpc.LoadCodingResponse;
import com.codecampus.coding.serialization.ProtoRedisSerializer;
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

        LettuceConnectionFactory connectionFactory =
                new LettuceConnectionFactory(host, port);
        connectionFactory.setPassword(password);
        return connectionFactory;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(
            RedisConnectionFactory cf) {

        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(cf);

        // Key = String, Value = JSON
        StringRedisSerializer keySerialize = new StringRedisSerializer();
        GenericJackson2JsonRedisSerializer valueSer =
                new GenericJackson2JsonRedisSerializer();

        redisTemplate.setKeySerializer(keySerialize);
        redisTemplate.setHashKeySerializer(keySerialize);

        redisTemplate.setValueSerializer(valueSer);
        redisTemplate.setHashValueSerializer(valueSer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean
    public RedisTemplate<String, LoadCodingResponse> loadCodingRedisTemplate(
            RedisConnectionFactory connectionFactory) {

        RedisTemplate<String, LoadCodingResponse> redisTemplate =
                new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);

        StringRedisSerializer keySerialization = new StringRedisSerializer();
        ProtoRedisSerializer valueSerialization = new ProtoRedisSerializer();

        redisTemplate.setKeySerializer(keySerialization);
        redisTemplate.setHashKeySerializer(keySerialization);
        redisTemplate.setValueSerializer(valueSerialization);
        redisTemplate.setHashValueSerializer(valueSerialization);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean(destroyMethod = "shutdown") // shutdown() sẽ được gọi khi Spring tắt
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
