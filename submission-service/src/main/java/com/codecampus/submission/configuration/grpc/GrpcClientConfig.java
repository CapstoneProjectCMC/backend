package com.codecampus.submission.configuration.grpc;

import com.codecampus.quiz.grpc.QuizServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcClientConfig
{
  @Bean(destroyMethod = "shutdownNow")
  ManagedChannel quizChannel(
      @Value("${grpc.quiz-service.host}") String host,
      @Value("${grpc.quiz-service.port}") int port)
  {
    return ManagedChannelBuilder.forAddress(host, port)
        .usePlaintext() // Truyền nội bộ, không sử dụng TLS
        .build();
  }

  @Bean
  QuizServiceGrpc.QuizServiceBlockingStub quizStub(
      ManagedChannel channel)
  {
    return QuizServiceGrpc.newBlockingStub(channel);
  }
}
