package com.codecampus.quiz.grpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.72.0)",
    comments = "Source: quiz.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class QuizServiceGrpc {

  private QuizServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "quiz.QuizService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.codecampus.quiz.grpc.ExerciseData,
      com.google.protobuf.Empty> getRegisterExerciseMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "RegisterExercise",
      requestType = com.codecampus.quiz.grpc.ExerciseData.class,
      responseType = com.google.protobuf.Empty.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.codecampus.quiz.grpc.ExerciseData,
      com.google.protobuf.Empty> getRegisterExerciseMethod() {
    io.grpc.MethodDescriptor<com.codecampus.quiz.grpc.ExerciseData, com.google.protobuf.Empty> getRegisterExerciseMethod;
    if ((getRegisterExerciseMethod = QuizServiceGrpc.getRegisterExerciseMethod) == null) {
      synchronized (QuizServiceGrpc.class) {
        if ((getRegisterExerciseMethod = QuizServiceGrpc.getRegisterExerciseMethod) == null) {
          QuizServiceGrpc.getRegisterExerciseMethod = getRegisterExerciseMethod =
              io.grpc.MethodDescriptor.<com.codecampus.quiz.grpc.ExerciseData, com.google.protobuf.Empty>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "RegisterExercise"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.codecampus.quiz.grpc.ExerciseData.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.protobuf.Empty.getDefaultInstance()))
              .setSchemaDescriptor(new QuizServiceMethodDescriptorSupplier("RegisterExercise"))
              .build();
        }
      }
    }
    return getRegisterExerciseMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.codecampus.quiz.grpc.QuizPayload,
      com.codecampus.quiz.grpc.QuizResult> getSubmitQuizMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "SubmitQuiz",
      requestType = com.codecampus.quiz.grpc.QuizPayload.class,
      responseType = com.codecampus.quiz.grpc.QuizResult.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.codecampus.quiz.grpc.QuizPayload,
      com.codecampus.quiz.grpc.QuizResult> getSubmitQuizMethod() {
    io.grpc.MethodDescriptor<com.codecampus.quiz.grpc.QuizPayload, com.codecampus.quiz.grpc.QuizResult> getSubmitQuizMethod;
    if ((getSubmitQuizMethod = QuizServiceGrpc.getSubmitQuizMethod) == null) {
      synchronized (QuizServiceGrpc.class) {
        if ((getSubmitQuizMethod = QuizServiceGrpc.getSubmitQuizMethod) == null) {
          QuizServiceGrpc.getSubmitQuizMethod = getSubmitQuizMethod =
              io.grpc.MethodDescriptor.<com.codecampus.quiz.grpc.QuizPayload, com.codecampus.quiz.grpc.QuizResult>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "SubmitQuiz"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.codecampus.quiz.grpc.QuizPayload.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.codecampus.quiz.grpc.QuizResult.getDefaultInstance()))
              .setSchemaDescriptor(new QuizServiceMethodDescriptorSupplier("SubmitQuiz"))
              .build();
        }
      }
    }
    return getSubmitQuizMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.codecampus.quiz.grpc.GetExerciseRequest,
      com.codecampus.quiz.grpc.ExerciseData> getGetExerciseMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetExercise",
      requestType = com.codecampus.quiz.grpc.GetExerciseRequest.class,
      responseType = com.codecampus.quiz.grpc.ExerciseData.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.codecampus.quiz.grpc.GetExerciseRequest,
      com.codecampus.quiz.grpc.ExerciseData> getGetExerciseMethod() {
    io.grpc.MethodDescriptor<com.codecampus.quiz.grpc.GetExerciseRequest, com.codecampus.quiz.grpc.ExerciseData> getGetExerciseMethod;
    if ((getGetExerciseMethod = QuizServiceGrpc.getGetExerciseMethod) == null) {
      synchronized (QuizServiceGrpc.class) {
        if ((getGetExerciseMethod = QuizServiceGrpc.getGetExerciseMethod) == null) {
          QuizServiceGrpc.getGetExerciseMethod = getGetExerciseMethod =
              io.grpc.MethodDescriptor.<com.codecampus.quiz.grpc.GetExerciseRequest, com.codecampus.quiz.grpc.ExerciseData>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetExercise"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.codecampus.quiz.grpc.GetExerciseRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.codecampus.quiz.grpc.ExerciseData.getDefaultInstance()))
              .setSchemaDescriptor(new QuizServiceMethodDescriptorSupplier("GetExercise"))
              .build();
        }
      }
    }
    return getGetExerciseMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static QuizServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<QuizServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<QuizServiceStub>() {
        @java.lang.Override
        public QuizServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new QuizServiceStub(channel, callOptions);
        }
      };
    return QuizServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports all types of calls on the service
   */
  public static QuizServiceBlockingV2Stub newBlockingV2Stub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<QuizServiceBlockingV2Stub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<QuizServiceBlockingV2Stub>() {
        @java.lang.Override
        public QuizServiceBlockingV2Stub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new QuizServiceBlockingV2Stub(channel, callOptions);
        }
      };
    return QuizServiceBlockingV2Stub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static QuizServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<QuizServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<QuizServiceBlockingStub>() {
        @java.lang.Override
        public QuizServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new QuizServiceBlockingStub(channel, callOptions);
        }
      };
    return QuizServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static QuizServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<QuizServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<QuizServiceFutureStub>() {
        @java.lang.Override
        public QuizServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new QuizServiceFutureStub(channel, callOptions);
        }
      };
    return QuizServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     */
    default void registerExercise(com.codecampus.quiz.grpc.ExerciseData request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getRegisterExerciseMethod(), responseObserver);
    }

    /**
     */
    default void submitQuiz(com.codecampus.quiz.grpc.QuizPayload request,
        io.grpc.stub.StreamObserver<com.codecampus.quiz.grpc.QuizResult> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getSubmitQuizMethod(), responseObserver);
    }

    /**
     */
    default void getExercise(com.codecampus.quiz.grpc.GetExerciseRequest request,
        io.grpc.stub.StreamObserver<com.codecampus.quiz.grpc.ExerciseData> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetExerciseMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service QuizService.
   */
  public static abstract class QuizServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return QuizServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service QuizService.
   */
  public static final class QuizServiceStub
      extends io.grpc.stub.AbstractAsyncStub<QuizServiceStub> {
    private QuizServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected QuizServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new QuizServiceStub(channel, callOptions);
    }

    /**
     */
    public void registerExercise(com.codecampus.quiz.grpc.ExerciseData request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getRegisterExerciseMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void submitQuiz(com.codecampus.quiz.grpc.QuizPayload request,
        io.grpc.stub.StreamObserver<com.codecampus.quiz.grpc.QuizResult> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getSubmitQuizMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getExercise(com.codecampus.quiz.grpc.GetExerciseRequest request,
        io.grpc.stub.StreamObserver<com.codecampus.quiz.grpc.ExerciseData> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetExerciseMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service QuizService.
   */
  public static final class QuizServiceBlockingV2Stub
      extends io.grpc.stub.AbstractBlockingStub<QuizServiceBlockingV2Stub> {
    private QuizServiceBlockingV2Stub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected QuizServiceBlockingV2Stub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new QuizServiceBlockingV2Stub(channel, callOptions);
    }

    /**
     */
    public com.google.protobuf.Empty registerExercise(com.codecampus.quiz.grpc.ExerciseData request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getRegisterExerciseMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.codecampus.quiz.grpc.QuizResult submitQuiz(com.codecampus.quiz.grpc.QuizPayload request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getSubmitQuizMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.codecampus.quiz.grpc.ExerciseData getExercise(com.codecampus.quiz.grpc.GetExerciseRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetExerciseMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do limited synchronous rpc calls to service QuizService.
   */
  public static final class QuizServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<QuizServiceBlockingStub> {
    private QuizServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected QuizServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new QuizServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.google.protobuf.Empty registerExercise(com.codecampus.quiz.grpc.ExerciseData request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getRegisterExerciseMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.codecampus.quiz.grpc.QuizResult submitQuiz(com.codecampus.quiz.grpc.QuizPayload request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getSubmitQuizMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.codecampus.quiz.grpc.ExerciseData getExercise(com.codecampus.quiz.grpc.GetExerciseRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetExerciseMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service QuizService.
   */
  public static final class QuizServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<QuizServiceFutureStub> {
    private QuizServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected QuizServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new QuizServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.protobuf.Empty> registerExercise(
        com.codecampus.quiz.grpc.ExerciseData request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getRegisterExerciseMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.codecampus.quiz.grpc.QuizResult> submitQuiz(
        com.codecampus.quiz.grpc.QuizPayload request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getSubmitQuizMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.codecampus.quiz.grpc.ExerciseData> getExercise(
        com.codecampus.quiz.grpc.GetExerciseRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetExerciseMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_REGISTER_EXERCISE = 0;
  private static final int METHODID_SUBMIT_QUIZ = 1;
  private static final int METHODID_GET_EXERCISE = 2;

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
        case METHODID_REGISTER_EXERCISE:
          serviceImpl.registerExercise((com.codecampus.quiz.grpc.ExerciseData) request,
              (io.grpc.stub.StreamObserver<com.google.protobuf.Empty>) responseObserver);
          break;
        case METHODID_SUBMIT_QUIZ:
          serviceImpl.submitQuiz((com.codecampus.quiz.grpc.QuizPayload) request,
              (io.grpc.stub.StreamObserver<com.codecampus.quiz.grpc.QuizResult>) responseObserver);
          break;
        case METHODID_GET_EXERCISE:
          serviceImpl.getExercise((com.codecampus.quiz.grpc.GetExerciseRequest) request,
              (io.grpc.stub.StreamObserver<com.codecampus.quiz.grpc.ExerciseData>) responseObserver);
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
          getRegisterExerciseMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.codecampus.quiz.grpc.ExerciseData,
              com.google.protobuf.Empty>(
                service, METHODID_REGISTER_EXERCISE)))
        .addMethod(
          getSubmitQuizMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.codecampus.quiz.grpc.QuizPayload,
              com.codecampus.quiz.grpc.QuizResult>(
                service, METHODID_SUBMIT_QUIZ)))
        .addMethod(
          getGetExerciseMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.codecampus.quiz.grpc.GetExerciseRequest,
              com.codecampus.quiz.grpc.ExerciseData>(
                service, METHODID_GET_EXERCISE)))
        .build();
  }

  private static abstract class QuizServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    QuizServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.codecampus.quiz.grpc.QuizProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("QuizService");
    }
  }

  private static final class QuizServiceFileDescriptorSupplier
      extends QuizServiceBaseDescriptorSupplier {
    QuizServiceFileDescriptorSupplier() {}
  }

  private static final class QuizServiceMethodDescriptorSupplier
      extends QuizServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    QuizServiceMethodDescriptorSupplier(java.lang.String methodName) {
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
      synchronized (QuizServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new QuizServiceFileDescriptorSupplier())
              .addMethod(getRegisterExerciseMethod())
              .addMethod(getSubmitQuizMethod())
              .addMethod(getGetExerciseMethod())
              .build();
        }
      }
    }
    return result;
  }
}
