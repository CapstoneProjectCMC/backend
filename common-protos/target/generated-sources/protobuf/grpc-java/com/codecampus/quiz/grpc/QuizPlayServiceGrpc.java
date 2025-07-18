package com.codecampus.quiz.grpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.72.0)",
    comments = "Source: quiz.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class QuizPlayServiceGrpc {

  private QuizPlayServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "quiz.QuizPlayService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.codecampus.quiz.grpc.LoadQuizRequest,
      com.codecampus.quiz.grpc.LoadQuizResponse> getLoadQuizMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "LoadQuiz",
      requestType = com.codecampus.quiz.grpc.LoadQuizRequest.class,
      responseType = com.codecampus.quiz.grpc.LoadQuizResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.codecampus.quiz.grpc.LoadQuizRequest,
      com.codecampus.quiz.grpc.LoadQuizResponse> getLoadQuizMethod() {
    io.grpc.MethodDescriptor<com.codecampus.quiz.grpc.LoadQuizRequest, com.codecampus.quiz.grpc.LoadQuizResponse> getLoadQuizMethod;
    if ((getLoadQuizMethod = QuizPlayServiceGrpc.getLoadQuizMethod) == null) {
      synchronized (QuizPlayServiceGrpc.class) {
        if ((getLoadQuizMethod = QuizPlayServiceGrpc.getLoadQuizMethod) == null) {
          QuizPlayServiceGrpc.getLoadQuizMethod = getLoadQuizMethod =
              io.grpc.MethodDescriptor.<com.codecampus.quiz.grpc.LoadQuizRequest, com.codecampus.quiz.grpc.LoadQuizResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "LoadQuiz"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.codecampus.quiz.grpc.LoadQuizRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.codecampus.quiz.grpc.LoadQuizResponse.getDefaultInstance()))
              .setSchemaDescriptor(new QuizPlayServiceMethodDescriptorSupplier("LoadQuiz"))
              .build();
        }
      }
    }
    return getLoadQuizMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.codecampus.quiz.grpc.SubmitQuizRequest,
      com.codecampus.quiz.grpc.SubmitQuizResponse> getSubmitQuizMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "SubmitQuiz",
      requestType = com.codecampus.quiz.grpc.SubmitQuizRequest.class,
      responseType = com.codecampus.quiz.grpc.SubmitQuizResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.codecampus.quiz.grpc.SubmitQuizRequest,
      com.codecampus.quiz.grpc.SubmitQuizResponse> getSubmitQuizMethod() {
    io.grpc.MethodDescriptor<com.codecampus.quiz.grpc.SubmitQuizRequest, com.codecampus.quiz.grpc.SubmitQuizResponse> getSubmitQuizMethod;
    if ((getSubmitQuizMethod = QuizPlayServiceGrpc.getSubmitQuizMethod) == null) {
      synchronized (QuizPlayServiceGrpc.class) {
        if ((getSubmitQuizMethod = QuizPlayServiceGrpc.getSubmitQuizMethod) == null) {
          QuizPlayServiceGrpc.getSubmitQuizMethod = getSubmitQuizMethod =
              io.grpc.MethodDescriptor.<com.codecampus.quiz.grpc.SubmitQuizRequest, com.codecampus.quiz.grpc.SubmitQuizResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "SubmitQuiz"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.codecampus.quiz.grpc.SubmitQuizRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.codecampus.quiz.grpc.SubmitQuizResponse.getDefaultInstance()))
              .setSchemaDescriptor(new QuizPlayServiceMethodDescriptorSupplier("SubmitQuiz"))
              .build();
        }
      }
    }
    return getSubmitQuizMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static QuizPlayServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<QuizPlayServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<QuizPlayServiceStub>() {
        @java.lang.Override
        public QuizPlayServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new QuizPlayServiceStub(channel, callOptions);
        }
      };
    return QuizPlayServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports all types of calls on the service
   */
  public static QuizPlayServiceBlockingV2Stub newBlockingV2Stub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<QuizPlayServiceBlockingV2Stub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<QuizPlayServiceBlockingV2Stub>() {
        @java.lang.Override
        public QuizPlayServiceBlockingV2Stub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new QuizPlayServiceBlockingV2Stub(channel, callOptions);
        }
      };
    return QuizPlayServiceBlockingV2Stub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static QuizPlayServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<QuizPlayServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<QuizPlayServiceBlockingStub>() {
        @java.lang.Override
        public QuizPlayServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new QuizPlayServiceBlockingStub(channel, callOptions);
        }
      };
    return QuizPlayServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static QuizPlayServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<QuizPlayServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<QuizPlayServiceFutureStub>() {
        @java.lang.Override
        public QuizPlayServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new QuizPlayServiceFutureStub(channel, callOptions);
        }
      };
    return QuizPlayServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     */
    default void loadQuiz(com.codecampus.quiz.grpc.LoadQuizRequest request,
        io.grpc.stub.StreamObserver<com.codecampus.quiz.grpc.LoadQuizResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getLoadQuizMethod(), responseObserver);
    }

    /**
     */
    default void submitQuiz(com.codecampus.quiz.grpc.SubmitQuizRequest request,
        io.grpc.stub.StreamObserver<com.codecampus.quiz.grpc.SubmitQuizResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getSubmitQuizMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service QuizPlayService.
   */
  public static abstract class QuizPlayServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return QuizPlayServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service QuizPlayService.
   */
  public static final class QuizPlayServiceStub
      extends io.grpc.stub.AbstractAsyncStub<QuizPlayServiceStub> {
    private QuizPlayServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected QuizPlayServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new QuizPlayServiceStub(channel, callOptions);
    }

    /**
     */
    public void loadQuiz(com.codecampus.quiz.grpc.LoadQuizRequest request,
        io.grpc.stub.StreamObserver<com.codecampus.quiz.grpc.LoadQuizResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getLoadQuizMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void submitQuiz(com.codecampus.quiz.grpc.SubmitQuizRequest request,
        io.grpc.stub.StreamObserver<com.codecampus.quiz.grpc.SubmitQuizResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getSubmitQuizMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service QuizPlayService.
   */
  public static final class QuizPlayServiceBlockingV2Stub
      extends io.grpc.stub.AbstractBlockingStub<QuizPlayServiceBlockingV2Stub> {
    private QuizPlayServiceBlockingV2Stub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected QuizPlayServiceBlockingV2Stub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new QuizPlayServiceBlockingV2Stub(channel, callOptions);
    }

    /**
     */
    public com.codecampus.quiz.grpc.LoadQuizResponse loadQuiz(com.codecampus.quiz.grpc.LoadQuizRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getLoadQuizMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.codecampus.quiz.grpc.SubmitQuizResponse submitQuiz(com.codecampus.quiz.grpc.SubmitQuizRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getSubmitQuizMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do limited synchronous rpc calls to service QuizPlayService.
   */
  public static final class QuizPlayServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<QuizPlayServiceBlockingStub> {
    private QuizPlayServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected QuizPlayServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new QuizPlayServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.codecampus.quiz.grpc.LoadQuizResponse loadQuiz(com.codecampus.quiz.grpc.LoadQuizRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getLoadQuizMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.codecampus.quiz.grpc.SubmitQuizResponse submitQuiz(com.codecampus.quiz.grpc.SubmitQuizRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getSubmitQuizMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service QuizPlayService.
   */
  public static final class QuizPlayServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<QuizPlayServiceFutureStub> {
    private QuizPlayServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected QuizPlayServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new QuizPlayServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.codecampus.quiz.grpc.LoadQuizResponse> loadQuiz(
        com.codecampus.quiz.grpc.LoadQuizRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getLoadQuizMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.codecampus.quiz.grpc.SubmitQuizResponse> submitQuiz(
        com.codecampus.quiz.grpc.SubmitQuizRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getSubmitQuizMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_LOAD_QUIZ = 0;
  private static final int METHODID_SUBMIT_QUIZ = 1;

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
        case METHODID_LOAD_QUIZ:
          serviceImpl.loadQuiz((com.codecampus.quiz.grpc.LoadQuizRequest) request,
              (io.grpc.stub.StreamObserver<com.codecampus.quiz.grpc.LoadQuizResponse>) responseObserver);
          break;
        case METHODID_SUBMIT_QUIZ:
          serviceImpl.submitQuiz((com.codecampus.quiz.grpc.SubmitQuizRequest) request,
              (io.grpc.stub.StreamObserver<com.codecampus.quiz.grpc.SubmitQuizResponse>) responseObserver);
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
          getLoadQuizMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.codecampus.quiz.grpc.LoadQuizRequest,
              com.codecampus.quiz.grpc.LoadQuizResponse>(
                service, METHODID_LOAD_QUIZ)))
        .addMethod(
          getSubmitQuizMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.codecampus.quiz.grpc.SubmitQuizRequest,
              com.codecampus.quiz.grpc.SubmitQuizResponse>(
                service, METHODID_SUBMIT_QUIZ)))
        .build();
  }

  private static abstract class QuizPlayServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    QuizPlayServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.codecampus.quiz.grpc.QuizProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("QuizPlayService");
    }
  }

  private static final class QuizPlayServiceFileDescriptorSupplier
      extends QuizPlayServiceBaseDescriptorSupplier {
    QuizPlayServiceFileDescriptorSupplier() {}
  }

  private static final class QuizPlayServiceMethodDescriptorSupplier
      extends QuizPlayServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    QuizPlayServiceMethodDescriptorSupplier(java.lang.String methodName) {
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
      synchronized (QuizPlayServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new QuizPlayServiceFileDescriptorSupplier())
              .addMethod(getLoadQuizMethod())
              .addMethod(getSubmitQuizMethod())
              .build();
        }
      }
    }
    return result;
  }
}
