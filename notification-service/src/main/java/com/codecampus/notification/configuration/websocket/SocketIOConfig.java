package com.codecampus.notification.configuration.websocket;

import com.corundumstudio.socketio.SocketIOServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SocketIOConfig {
  @Bean
  public SocketIOServer socketIOServer() {
    com.corundumstudio.socketio.Configuration configuration =
        new com.corundumstudio.socketio.Configuration();
    configuration.setPort(4101);
    configuration.setOrigin("*");

    return new SocketIOServer(configuration);
  }
}
