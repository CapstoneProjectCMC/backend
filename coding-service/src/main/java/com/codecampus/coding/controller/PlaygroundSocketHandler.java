package com.codecampus.coding.controller;

import com.codecampus.coding.dto.data.PlaygroundRunPayload;
import com.codecampus.coding.dto.data.PlaygroundUpdatePayload;
import com.codecampus.coding.grpc.playground.PlaygroundServiceGrpc;
import com.codecampus.coding.grpc.playground.RunRequest;
import com.codecampus.coding.grpc.playground.RunUpdate;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import io.grpc.stub.ClientCallStreamObserver;
import io.grpc.stub.ClientResponseObserver;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PlaygroundSocketHandler {

  SocketIOServer server;
  PlaygroundServiceGrpc.PlaygroundServiceStub playgroundStub;

  /**
   * Lưu handle để có thể cancel khi client hủy / đóng
   */
  ConcurrentMap<UUID, ClientCallStreamObserver<?>> running =
      new ConcurrentHashMap<>();

  private static String normalize(String s) {
    return s == null ? "" : s;
  }

  @PostConstruct
  public void start() {
    server.addListeners(this); // Dùng @OnConnect / @OnEvent
    server.start();
    log.info("Socket.IO server started on port {}",
        server.getConfiguration().getPort());
  }

  @PreDestroy
  public void stop() {
    try {
      server.stop();
    } catch (Exception ignored) {
    }
  }

  @OnConnect
  public void onConnect(
      SocketIOClient client) {
    log.info("Client connected: {}", client.getSessionId());
  }

  @OnDisconnect
  public void onDisconnect(SocketIOClient client) {
    log.info("Client disconnected: {}", client.getSessionId());
    cancelIfRunning(client.getSessionId(), "Client disconnected");
  }

  @OnEvent("playground:run")
  public void run(
      SocketIOClient client,
      PlaygroundRunPayload payload) {
    log.info("Run request from {}: {}", client.getSessionId(), payload);

    RunRequest runRequest = RunRequest.newBuilder()
        .setLanguage(normalize(payload.getLanguage()))
        .setSourceCode(normalize(payload.getSourceCode()))
        .setStdin(normalize(payload.getStdin()))
        .setMemoryMb(
            (payload.getMemoryMb() == null || payload.getMemoryMb() <= 0) ?
                256 : payload.getMemoryMb())
        .setCpus((payload.getCpus() == null || payload.getCpus() <= 0f) ? 0.5f :
            payload.getCpus())
        .setTimeLimitSec((payload.getTimeLimitSec() == null ||
            payload.getTimeLimitSec() <= 0) ? 5 : payload.getTimeLimitSec())
        .build();

    // Dùng ClientResponseObserver để giữ reference cancel.
    ClientResponseObserver<RunRequest, RunUpdate> obs =
        new ClientResponseObserver<>() {

          private ClientCallStreamObserver<RunRequest> call;

          @Override
          public void onNext(RunUpdate runUpdate) {
            client.sendEvent("playground:run",
                PlaygroundUpdatePayload.toPlaygroundUpdatePayloadFromRunUpdate(
                    runUpdate));

            switch (runUpdate.getPhase()) {
              case FINISHED, ERROR:
                client.sendEvent("playground:finished",
                    PlaygroundUpdatePayload.toPlaygroundUpdatePayloadFromRunUpdate(
                        runUpdate));
            }
          }

          @Override
          public void onError(Throwable throwable) {
            log.warn("gRPC error for {}: {}", client.getSessionId(),
                throwable.toString());
            client.sendEvent("playground:error", throwable.getMessage());
            running.remove(client.getSessionId());
          }

          @Override
          public void onCompleted() {
            log.info("gRPC stream completed for {}", client.getSessionId());
            running.remove(client.getSessionId());
          }

          @Override
          public void beforeStart(
              ClientCallStreamObserver<RunRequest> clientCallStreamObserver) {
            this.call = clientCallStreamObserver;
            running.put(client.getSessionId(), clientCallStreamObserver);
          }
        };
    playgroundStub.run(runRequest, obs);
  }

  @OnEvent("playground:cancel")
  public void cancel(SocketIOClient client) {
    boolean canceled =
        cancelIfRunning(client.getSessionId(), "cancel by client");
    if (canceled) {
      client.sendEvent("playground:canceled");
    }
  }

  private boolean cancelIfRunning(UUID sessionId, String reason) {
    ClientCallStreamObserver<?> clientCallStreamObserver =
        (ClientCallStreamObserver<?>) running.remove(sessionId);
    if (clientCallStreamObserver != null) {
      try {
        clientCallStreamObserver.cancel(reason, null);
      } catch (Exception ignored) {
      }
      return true;
    }
    return false;
  }
}
