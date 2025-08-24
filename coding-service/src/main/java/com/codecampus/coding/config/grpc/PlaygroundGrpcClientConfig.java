package com.codecampus.coding.config.grpc;

import com.codecampus.coding.grpc.playground.PlaygroundServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PlaygroundGrpcClientConfig {
  @Bean(destroyMethod = "shutdownNow")
  ManagedChannel playgroundChannel(
      @Value("${grpc.server.port}") int port) {
    return ManagedChannelBuilder.forAddress("127.0.0.1", port)
        .usePlaintext()
        .keepAliveWithoutCalls(true)
        .build();
  }

  @Bean
  PlaygroundServiceGrpc.PlaygroundServiceStub playgroundStub(
      ManagedChannel playgroundChannel) {
    return PlaygroundServiceGrpc.newStub(playgroundChannel);
  }
}
