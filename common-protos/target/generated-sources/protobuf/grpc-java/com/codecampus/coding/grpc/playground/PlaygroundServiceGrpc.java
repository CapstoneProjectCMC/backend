package com.codecampus.coding.grpc.playground;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.72.0)",
    comments = "Source: playground.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class PlaygroundServiceGrpc {

  private PlaygroundServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "coding.playground.PlaygroundService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.codecampus.coding.grpc.playground.RunRequest,
      com.codecampus.coding.grpc.playground.RunUpdate> getRunMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Run",
      requestType = com.codecampus.coding.grpc.playground.RunRequest.class,
      responseType = com.codecampus.coding.grpc.playground.RunUpdate.class,
      methodType = io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
  public static io.grpc.MethodDescriptor<com.codecampus.coding.grpc.playground.RunRequest,
      com.codecampus.coding.grpc.playground.RunUpdate> getRunMethod() {
    io.grpc.MethodDescriptor<com.codecampus.coding.grpc.playground.RunRequest, com.codecampus.coding.grpc.playground.RunUpdate> getRunMethod;
    if ((getRunMethod = PlaygroundServiceGrpc.getRunMethod) == null) {
      synchronized (PlaygroundServiceGrpc.class) {
        if ((getRunMethod = PlaygroundServiceGrpc.getRunMethod) == null) {
          PlaygroundServiceGrpc.getRunMethod = getRunMethod =
              io.grpc.MethodDescriptor.<com.codecampus.coding.grpc.playground.RunRequest, com.codecampus.coding.grpc.playground.RunUpdate>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Run"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.codecampus.coding.grpc.playground.RunRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.codecampus.coding.grpc.playground.RunUpdate.getDefaultInstance()))
              .setSchemaDescriptor(new PlaygroundServiceMethodDescriptorSupplier("Run"))
              .build();
        }
      }
    }
    return getRunMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.google.protobuf.Empty,
      com.google.protobuf.Empty> getCancelMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Cancel",
      requestType = com.google.protobuf.Empty.class,
      responseType = com.google.protobuf.Empty.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.google.protobuf.Empty,
      com.google.protobuf.Empty> getCancelMethod() {
    io.grpc.MethodDescriptor<com.google.protobuf.Empty, com.google.protobuf.Empty> getCancelMethod;
    if ((getCancelMethod = PlaygroundServiceGrpc.getCancelMethod) == null) {
      synchronized (PlaygroundServiceGrpc.class) {
        if ((getCancelMethod = PlaygroundServiceGrpc.getCancelMethod) == null) {
          PlaygroundServiceGrpc.getCancelMethod = getCancelMethod =
              io.grpc.MethodDescriptor.<com.google.protobuf.Empty, com.google.protobuf.Empty>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Cancel"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.protobuf.Empty.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.protobuf.Empty.getDefaultInstance()))
              .setSchemaDescriptor(new PlaygroundServiceMethodDescriptorSupplier("Cancel"))
              .build();
        }
      }
    }
    return getCancelMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static PlaygroundServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<PlaygroundServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<PlaygroundServiceStub>() {
        @java.lang.Override
        public PlaygroundServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new PlaygroundServiceStub(channel, callOptions);
        }
      };
    return PlaygroundServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports all types of calls on the service
   */
  public static PlaygroundServiceBlockingV2Stub newBlockingV2Stub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<PlaygroundServiceBlockingV2Stub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<PlaygroundServiceBlockingV2Stub>() {
        @java.lang.Override
        public PlaygroundServiceBlockingV2Stub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new PlaygroundServiceBlockingV2Stub(channel, callOptions);
        }
      };
    return PlaygroundServiceBlockingV2Stub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static PlaygroundServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<PlaygroundServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<PlaygroundServiceBlockingStub>() {
        @java.lang.Override
        public PlaygroundServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new PlaygroundServiceBlockingStub(channel, callOptions);
        }
      };
    return PlaygroundServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static PlaygroundServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<PlaygroundServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<PlaygroundServiceFutureStub>() {
        @java.lang.Override
        public PlaygroundServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new PlaygroundServiceFutureStub(channel, callOptions);
        }
      };
    return PlaygroundServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     * <pre>
     * Server-streaming: gửi log/STDOUT theo thời gian thực
     * </pre>
     */
    default void run(com.codecampus.coding.grpc.playground.RunRequest request,
        io.grpc.stub.StreamObserver<com.codecampus.coding.grpc.playground.RunUpdate> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getRunMethod(), responseObserver);
    }

    /**
     * <pre>
     * (Tuỳ chọn) Hủy job nếu client không kịp cancel stream
     * </pre>
     */
    default void cancel(com.google.protobuf.Empty request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getCancelMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service PlaygroundService.
   */
  public static abstract class PlaygroundServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return PlaygroundServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service PlaygroundService.
   */
  public static final class PlaygroundServiceStub
      extends io.grpc.stub.AbstractAsyncStub<PlaygroundServiceStub> {
    private PlaygroundServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected PlaygroundServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new PlaygroundServiceStub(channel, callOptions);
    }

    /**
     * <pre>
     * Server-streaming: gửi log/STDOUT theo thời gian thực
     * </pre>
     */
    public void run(com.codecampus.coding.grpc.playground.RunRequest request,
        io.grpc.stub.StreamObserver<com.codecampus.coding.grpc.playground.RunUpdate> responseObserver) {
      io.grpc.stub.ClientCalls.asyncServerStreamingCall(
          getChannel().newCall(getRunMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * (Tuỳ chọn) Hủy job nếu client không kịp cancel stream
     * </pre>
     */
    public void cancel(com.google.protobuf.Empty request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getCancelMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service PlaygroundService.
   */
  public static final class PlaygroundServiceBlockingV2Stub
      extends io.grpc.stub.AbstractBlockingStub<PlaygroundServiceBlockingV2Stub> {
    private PlaygroundServiceBlockingV2Stub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected PlaygroundServiceBlockingV2Stub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new PlaygroundServiceBlockingV2Stub(channel, callOptions);
    }

    /**
     * <pre>
     * Server-streaming: gửi log/STDOUT theo thời gian thực
     * </pre>
     */
    @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/10918")
    public io.grpc.stub.BlockingClientCall<?, com.codecampus.coding.grpc.playground.RunUpdate>
        run(com.codecampus.coding.grpc.playground.RunRequest request) {
      return io.grpc.stub.ClientCalls.blockingV2ServerStreamingCall(
          getChannel(), getRunMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * (Tuỳ chọn) Hủy job nếu client không kịp cancel stream
     * </pre>
     */
    public com.google.protobuf.Empty cancel(com.google.protobuf.Empty request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getCancelMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do limited synchronous rpc calls to service PlaygroundService.
   */
  public static final class PlaygroundServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<PlaygroundServiceBlockingStub> {
    private PlaygroundServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected PlaygroundServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new PlaygroundServiceBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * Server-streaming: gửi log/STDOUT theo thời gian thực
     * </pre>
     */
    public java.util.Iterator<com.codecampus.coding.grpc.playground.RunUpdate> run(
        com.codecampus.coding.grpc.playground.RunRequest request) {
      return io.grpc.stub.ClientCalls.blockingServerStreamingCall(
          getChannel(), getRunMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * (Tuỳ chọn) Hủy job nếu client không kịp cancel stream
     * </pre>
     */
    public com.google.protobuf.Empty cancel(com.google.protobuf.Empty request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getCancelMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service PlaygroundService.
   */
  public static final class PlaygroundServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<PlaygroundServiceFutureStub> {
    private PlaygroundServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected PlaygroundServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new PlaygroundServiceFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * (Tuỳ chọn) Hủy job nếu client không kịp cancel stream
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.protobuf.Empty> cancel(
        com.google.protobuf.Empty request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getCancelMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_RUN = 0;
  private static final int METHODID_CANCEL = 1;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_RUN:
          serviceImpl.run((com.codecampus.coding.grpc.playground.RunRequest) request,
              (io.grpc.stub.StreamObserver<com.codecampus.coding.grpc.playground.RunUpdate>) responseObserver);
          break;
        case METHODID_CANCEL:
          serviceImpl.cancel((com.google.protobuf.Empty) request,
              (io.grpc.stub.StreamObserver<com.google.protobuf.Empty>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getRunMethod(),
          io.grpc.stub.ServerCalls.asyncServerStreamingCall(
            new MethodHandlers<
              com.codecampus.coding.grpc.playground.RunRequest,
              com.codecampus.coding.grpc.playground.RunUpdate>(
                service, METHODID_RUN)))
        .addMethod(
          getCancelMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.google.protobuf.Empty,
              com.google.protobuf.Empty>(
                service, METHODID_CANCEL)))
        .build();
  }

  private static abstract class PlaygroundServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    PlaygroundServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.codecampus.coding.grpc.playground.PlaygroundProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("PlaygroundService");
    }
  }

  private static final class PlaygroundServiceFileDescriptorSupplier
      extends PlaygroundServiceBaseDescriptorSupplier {
    PlaygroundServiceFileDescriptorSupplier() {}
  }

  private static final class PlaygroundServiceMethodDescriptorSupplier
      extends PlaygroundServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    PlaygroundServiceMethodDescriptorSupplier(java.lang.String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (PlaygroundServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new PlaygroundServiceFileDescriptorSupplier())
              .addMethod(getRunMethod())
              .addMethod(getCancelMethod())
              .build();
        }
      }
    }
    return result;
  }
}
