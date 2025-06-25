package com.codecampus.quiz.config.kafka;

import event.ExerciseCreatedEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;

@EnableKafka
@Configuration
public class KafkaConfig
{
  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, ExerciseCreatedEvent> kafkaListenerContainerFactory(
      ConsumerFactory<String, ExerciseCreatedEvent> consumerFactory)
  {
    return new ConcurrentKafkaListenerContainerFactory<>();
  }
}