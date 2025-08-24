package com.codecampus.quiz.configuration.grpc;

import com.codecampus.submission.grpc.SubmissionSyncServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcClientConfig {
  @Bean(destroyMethod = "shutdownNow")
  ManagedChannel submissionChannel(
      @Value("${submission.grpc.host}") String host,
      @Value("${submission.grpc.port}") int port) {
    return ManagedChannelBuilder.forAddress(host, port)
        .usePlaintext()
        .build();
  }

  @Bean
  SubmissionSyncServiceGrpc.SubmissionSyncServiceBlockingStub submissionStub(
      ManagedChannel submissionChannel) {
    return SubmissionSyncServiceGrpc.newBlockingStub(submissionChannel);
  }

}
