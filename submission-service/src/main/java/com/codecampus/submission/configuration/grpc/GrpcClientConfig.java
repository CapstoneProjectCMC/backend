package com.codecampus.submission.configuration.grpc;

import com.codecampus.coding.grpc.CodingSyncServiceGrpc;
import com.codecampus.quiz.grpc.QuizSyncServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcClientConfig {
  // ----- Quiz Service -----
  @Bean(destroyMethod = "shutdownNow")
  public ManagedChannel quizChannel(
      @Value("${quiz.grpc.host}") String host,
      @Value("${quiz.grpc.port}") int port) {
    return ManagedChannelBuilder.forAddress(host, port)
        .usePlaintext()
        .build();
  }

  @Bean
  public QuizSyncServiceGrpc.QuizSyncServiceBlockingStub quizStub(
      ManagedChannel quizChannel) {
    return QuizSyncServiceGrpc.newBlockingStub(quizChannel);
  }

  // ----- Coding Service -----
  @Bean(destroyMethod = "shutdownNow")
  ManagedChannel codingChannel(
      @Value("${coding.grpc.host}") String host,
      @Value("${coding.grpc.port}") int port) {
    return ManagedChannelBuilder.forAddress(host, port)
        .usePlaintext()
        .build();
  }

  @Bean
  CodingSyncServiceGrpc.CodingSyncServiceBlockingStub codingStub(
      ManagedChannel codingChannel) {
    return CodingSyncServiceGrpc.newBlockingStub(codingChannel);
  }
}
