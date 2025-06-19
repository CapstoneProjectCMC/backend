package com.codecampus.identity.configuration.config;

import org.apache.catalina.connector.Connector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DualPortConfig {
  @Value("${server.http.port}")
  private int httpPort;

  @Bean
  public ServletWebServerFactory servletContainer() {
    // Tomcat là default container của Spring Boot Starter Web
    TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();

    Connector httpConnector =
        new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
    httpConnector.setScheme("http");
    httpConnector.setPort(httpPort);
    httpConnector.setSecure(false);

    factory.addAdditionalTomcatConnectors(httpConnector);
    return factory;
  }
}