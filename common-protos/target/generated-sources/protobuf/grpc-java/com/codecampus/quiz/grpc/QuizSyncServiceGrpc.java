package com.codecampus.quiz.grpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.72.0)",
    comments = "Source: quiz.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class QuizSyncServiceGrpc {

  private QuizSyncServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "quiz.QuizSyncService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.codecampus.quiz.grpc.CreateQuizExerciseRequest,
      com.google.protobuf.Empty> getCreateQuizExerciseMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "CreateQuizExercise",
      requestType = com.codecampus.quiz.grpc.CreateQuizExerciseRequest.class,
      responseType = com.google.protobuf.Empty.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.codecampus.quiz.grpc.CreateQuizExerciseRequest,
      com.google.protobuf.Empty> getCreateQuizExerciseMethod() {
    io.grpc.MethodDescriptor<com.codecampus.quiz.grpc.CreateQuizExerciseRequest, com.google.protobuf.Empty> getCreateQuizExerciseMethod;
    if ((getCreateQuizExerciseMethod = QuizSyncServiceGrpc.getCreateQuizExerciseMethod) == null) {
      synchronized (QuizSyncServiceGrpc.class) {
        if ((getCreateQuizExerciseMethod = QuizSyncServiceGrpc.getCreateQuizExerciseMethod) == null) {
          QuizSyncServiceGrpc.getCreateQuizExerciseMethod = getCreateQuizExerciseMethod =
              io.grpc.MethodDescriptor.<com.codecampus.quiz.grpc.CreateQuizExerciseRequest, com.google.protobuf.Empty>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "CreateQuizExercise"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.codecampus.quiz.grpc.CreateQuizExerciseRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.protobuf.Empty.getDefaultInstance()))
              .setSchemaDescriptor(new QuizSyncServiceMethodDescriptorSupplier("CreateQuizExercise"))
              .build();
        }
      }
    }
    return getCreateQuizExerciseMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.codecampus.quiz.grpc.AddQuizDetailRequest,
      com.google.protobuf.Empty> getAddQuizDetailMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "AddQuizDetail",
      requestType = com.codecampus.quiz.grpc.AddQuizDetailRequest.class,
      responseType = com.google.protobuf.Empty.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.codecampus.quiz.grpc.AddQuizDetailRequest,
      com.google.protobuf.Empty> getAddQuizDetailMethod() {
    io.grpc.MethodDescriptor<com.codecampus.quiz.grpc.AddQuizDetailRequest, com.google.protobuf.Empty> getAddQuizDetailMethod;
    if ((getAddQuizDetailMethod = QuizSyncServiceGrpc.getAddQuizDetailMethod) == null) {
      synchronized (QuizSyncServiceGrpc.class) {
        if ((getAddQuizDetailMethod = QuizSyncServiceGrpc.getAddQuizDetailMethod) == null) {
          QuizSyncServiceGrpc.getAddQuizDetailMethod = getAddQuizDetailMethod =
              io.grpc.MethodDescriptor.<com.codecampus.quiz.grpc.AddQuizDetailRequest, com.google.protobuf.Empty>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "AddQuizDetail"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.codecampus.quiz.grpc.AddQuizDetailRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.protobuf.Empty.getDefaultInstance()))
              .setSchemaDescriptor(new QuizSyncServiceMethodDescriptorSupplier("AddQuizDetail"))
              .build();
        }
      }
    }
    return getAddQuizDetailMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.codecampus.quiz.grpc.AddQuestionRequest,
      com.google.protobuf.Empty> getAddQuestionMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "AddQuestion",
      requestType = com.codecampus.quiz.grpc.AddQuestionRequest.class,
      responseType = com.google.protobuf.Empty.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.codecampus.quiz.grpc.AddQuestionRequest,
      com.google.protobuf.Empty> getAddQuestionMethod() {
    io.grpc.MethodDescriptor<com.codecampus.quiz.grpc.AddQuestionRequest, com.google.protobuf.Empty> getAddQuestionMethod;
    if ((getAddQuestionMethod = QuizSyncServiceGrpc.getAddQuestionMethod) == null) {
      synchronized (QuizSyncServiceGrpc.class) {
        if ((getAddQuestionMethod = QuizSyncServiceGrpc.getAddQuestionMethod) == null) {
          QuizSyncServiceGrpc.getAddQuestionMethod = getAddQuestionMethod =
              io.grpc.MethodDescriptor.<com.codecampus.quiz.grpc.AddQuestionRequest, com.google.protobuf.Empty>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "AddQuestion"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.codecampus.quiz.grpc.AddQuestionRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.protobuf.Empty.getDefaultInstance()))
              .setSchemaDescriptor(new QuizSyncServiceMethodDescriptorSupplier("AddQuestion"))
              .build();
        }
      }
    }
    return getAddQuestionMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.codecampus.quiz.grpc.AddOptionRequest,
      com.google.protobuf.Empty> getAddOptionMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "AddOption",
      requestType = com.codecampus.quiz.grpc.AddOptionRequest.class,
      responseType = com.google.protobuf.Empty.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.codecampus.quiz.grpc.AddOptionRequest,
      com.google.protobuf.Empty> getAddOptionMethod() {
    io.grpc.MethodDescriptor<com.codecampus.quiz.grpc.AddOptionRequest, com.google.protobuf.Empty> getAddOptionMethod;
    if ((getAddOptionMethod = QuizSyncServiceGrpc.getAddOptionMethod) == null) {
      synchronized (QuizSyncServiceGrpc.class) {
        if ((getAddOptionMethod = QuizSyncServiceGrpc.getAddOptionMethod) == null) {
          QuizSyncServiceGrpc.getAddOptionMethod = getAddOptionMethod =
              io.grpc.MethodDescriptor.<com.codecampus.quiz.grpc.AddOptionRequest, com.google.protobuf.Empty>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "AddOption"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.codecampus.quiz.grpc.AddOptionRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.protobuf.Empty.getDefaultInstance()))
              .setSchemaDescriptor(new QuizSyncServiceMethodDescriptorSupplier("AddOption"))
              .build();
        }
      }
    }
    return getAddOptionMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.codecampus.quiz.grpc.UpsertAssignmentRequest,
      com.google.protobuf.Empty> getUpsertAssignmentMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "UpsertAssignment",
      requestType = com.codecampus.quiz.grpc.UpsertAssignmentRequest.class,
      responseType = com.google.protobuf.Empty.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.codecampus.quiz.grpc.UpsertAssignmentRequest,
      com.google.protobuf.Empty> getUpsertAssignmentMethod() {
    io.grpc.MethodDescriptor<com.codecampus.quiz.grpc.UpsertAssignmentRequest, com.google.protobuf.Empty> getUpsertAssignmentMethod;
    if ((getUpsertAssignmentMethod = QuizSyncServiceGrpc.getUpsertAssignmentMethod) == null) {
      synchronized (QuizSyncServiceGrpc.class) {
        if ((getUpsertAssignmentMethod = QuizSyncServiceGrpc.getUpsertAssignmentMethod) == null) {
          QuizSyncServiceGrpc.getUpsertAssignmentMethod = getUpsertAssignmentMethod =
              io.grpc.MethodDescriptor.<com.codecampus.quiz.grpc.UpsertAssignmentRequest, com.google.protobuf.Empty>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "UpsertAssignment"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.codecampus.quiz.grpc.UpsertAssignmentRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.protobuf.Empty.getDefaultInstance()))
              .setSchemaDescriptor(new QuizSyncServiceMethodDescriptorSupplier("UpsertAssignment"))
              .build();
        }
      }
    }
    return getUpsertAssignmentMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.codecampus.quiz.grpc.SoftDeleteRequest,
      com.google.protobuf.Empty> getSoftDeleteExerciseMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "SoftDeleteExercise",
      requestType = com.codecampus.quiz.grpc.SoftDeleteRequest.class,
      responseType = com.google.protobuf.Empty.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.codecampus.quiz.grpc.SoftDeleteRequest,
      com.google.protobuf.Empty> getSoftDeleteExerciseMethod() {
    io.grpc.MethodDescriptor<com.codecampus.quiz.grpc.SoftDeleteRequest, com.google.protobuf.Empty> getSoftDeleteExerciseMethod;
    if ((getSoftDeleteExerciseMethod = QuizSyncServiceGrpc.getSoftDeleteExerciseMethod) == null) {
      synchronized (QuizSyncServiceGrpc.class) {
        if ((getSoftDeleteExerciseMethod = QuizSyncServiceGrpc.getSoftDeleteExerciseMethod) == null) {
          QuizSyncServiceGrpc.getSoftDeleteExerciseMethod = getSoftDeleteExerciseMethod =
              io.grpc.MethodDescriptor.<com.codecampus.quiz.grpc.SoftDeleteRequest, com.google.protobuf.Empty>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "SoftDeleteExercise"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.codecampus.quiz.grpc.SoftDeleteRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.protobuf.Empty.getDefaultInstance()))
              .setSchemaDescriptor(new QuizSyncServiceMethodDescriptorSupplier("SoftDeleteExercise"))
              .build();
        }
      }
    }
    return getSoftDeleteExerciseMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.codecampus.quiz.grpc.SoftDeleteQuestionRequest,
      com.google.protobuf.Empty> getSoftDeleteQuestionMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "SoftDeleteQuestion",
      requestType = com.codecampus.quiz.grpc.SoftDeleteQuestionRequest.class,
      responseType = com.google.protobuf.Empty.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.codecampus.quiz.grpc.SoftDeleteQuestionRequest,
      com.google.protobuf.Empty> getSoftDeleteQuestionMethod() {
    io.grpc.MethodDescriptor<com.codecampus.quiz.grpc.SoftDeleteQuestionRequest, com.google.protobuf.Empty> getSoftDeleteQuestionMethod;
    if ((getSoftDeleteQuestionMethod = QuizSyncServiceGrpc.getSoftDeleteQuestionMethod) == null) {
      synchronized (QuizSyncServiceGrpc.class) {
        if ((getSoftDeleteQuestionMethod = QuizSyncServiceGrpc.getSoftDeleteQuestionMethod) == null) {
          QuizSyncServiceGrpc.getSoftDeleteQuestionMethod = getSoftDeleteQuestionMethod =
              io.grpc.MethodDescriptor.<com.codecampus.quiz.grpc.SoftDeleteQuestionRequest, com.google.protobuf.Empty>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "SoftDeleteQuestion"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.codecampus.quiz.grpc.SoftDeleteQuestionRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.protobuf.Empty.getDefaultInstance()))
              .setSchemaDescriptor(new QuizSyncServiceMethodDescriptorSupplier("SoftDeleteQuestion"))
              .build();
        }
      }
    }
    return getSoftDeleteQuestionMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.codecampus.quiz.grpc.SoftDeleteOptionRequest,
      com.google.protobuf.Empty> getSoftDeleteOptionMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "SoftDeleteOption",
      requestType = com.codecampus.quiz.grpc.SoftDeleteOptionRequest.class,
      responseType = com.google.protobuf.Empty.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.codecampus.quiz.grpc.SoftDeleteOptionRequest,
      com.google.protobuf.Empty> getSoftDeleteOptionMethod() {
    io.grpc.MethodDescriptor<com.codecampus.quiz.grpc.SoftDeleteOptionRequest, com.google.protobuf.Empty> getSoftDeleteOptionMethod;
    if ((getSoftDeleteOptionMethod = QuizSyncServiceGrpc.getSoftDeleteOptionMethod) == null) {
      synchronized (QuizSyncServiceGrpc.class) {
        if ((getSoftDeleteOptionMethod = QuizSyncServiceGrpc.getSoftDeleteOptionMethod) == null) {
          QuizSyncServiceGrpc.getSoftDeleteOptionMethod = getSoftDeleteOptionMethod =
              io.grpc.MethodDescriptor.<com.codecampus.quiz.grpc.SoftDeleteOptionRequest, com.google.protobuf.Empty>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "SoftDeleteOption"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.codecampus.quiz.grpc.SoftDeleteOptionRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.protobuf.Empty.getDefaultInstance()))
              .setSchemaDescriptor(new QuizSyncServiceMethodDescriptorSupplier("SoftDeleteOption"))
              .build();
        }
      }
    }
    return getSoftDeleteOptionMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static QuizSyncServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<QuizSyncServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<QuizSyncServiceStub>() {
        @java.lang.Override
        public QuizSyncServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new QuizSyncServiceStub(channel, callOptions);
        }
      };
    return QuizSyncServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports all types of calls on the service
   */
  public static QuizSyncServiceBlockingV2Stub newBlockingV2Stub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<QuizSyncServiceBlockingV2Stub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<QuizSyncServiceBlockingV2Stub>() {
        @java.lang.Override
        public QuizSyncServiceBlockingV2Stub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new QuizSyncServiceBlockingV2Stub(channel, callOptions);
        }
      };
    return QuizSyncServiceBlockingV2Stub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static QuizSyncServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<QuizSyncServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<QuizSyncServiceBlockingStub>() {
        @java.lang.Override
        public QuizSyncServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new QuizSyncServiceBlockingStub(channel, callOptions);
        }
      };
    return QuizSyncServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static QuizSyncServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<QuizSyncServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<QuizSyncServiceFutureStub>() {
        @java.lang.Override
        public QuizSyncServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new QuizSyncServiceFutureStub(channel, callOptions);
        }
      };
    return QuizSyncServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     */
    default void createQuizExercise(com.codecampus.quiz.grpc.CreateQuizExerciseRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getCreateQuizExerciseMethod(), responseObserver);
    }

    /**
     */
    default void addQuizDetail(com.codecampus.quiz.grpc.AddQuizDetailRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getAddQuizDetailMethod(), responseObserver);
    }

    /**
     */
    default void addQuestion(com.codecampus.quiz.grpc.AddQuestionRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getAddQuestionMethod(), responseObserver);
    }

    /**
     */
    default void addOption(com.codecampus.quiz.grpc.AddOptionRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getAddOptionMethod(), responseObserver);
    }

    /**
     */
    default void upsertAssignment(com.codecampus.quiz.grpc.UpsertAssignmentRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getUpsertAssignmentMethod(), responseObserver);
    }

    /**
     */
    default void softDeleteExercise(com.codecampus.quiz.grpc.SoftDeleteRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getSoftDeleteExerciseMethod(), responseObserver);
    }

    /**
     */
    default void softDeleteQuestion(com.codecampus.quiz.grpc.SoftDeleteQuestionRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getSoftDeleteQuestionMethod(), responseObserver);
    }

    /**
     */
    default void softDeleteOption(com.codecampus.quiz.grpc.SoftDeleteOptionRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getSoftDeleteOptionMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service QuizSyncService.
   */
  public static abstract class QuizSyncServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return QuizSyncServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service QuizSyncService.
   */
  public static final class QuizSyncServiceStub
      extends io.grpc.stub.AbstractAsyncStub<QuizSyncServiceStub> {
    private QuizSyncServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected QuizSyncServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new QuizSyncServiceStub(channel, callOptions);
    }

    /**
     */
    public void createQuizExercise(com.codecampus.quiz.grpc.CreateQuizExerciseRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getCreateQuizExerciseMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void addQuizDetail(com.codecampus.quiz.grpc.AddQuizDetailRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getAddQuizDetailMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void addQuestion(com.codecampus.quiz.grpc.AddQuestionRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getAddQuestionMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void addOption(com.codecampus.quiz.grpc.AddOptionRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getAddOptionMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void upsertAssignment(com.codecampus.quiz.grpc.UpsertAssignmentRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getUpsertAssignmentMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void softDeleteExercise(com.codecampus.quiz.grpc.SoftDeleteRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getSoftDeleteExerciseMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void softDeleteQuestion(com.codecampus.quiz.grpc.SoftDeleteQuestionRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getSoftDeleteQuestionMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void softDeleteOption(com.codecampus.quiz.grpc.SoftDeleteOptionRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getSoftDeleteOptionMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service QuizSyncService.
   */
  public static final class QuizSyncServiceBlockingV2Stub
      extends io.grpc.stub.AbstractBlockingStub<QuizSyncServiceBlockingV2Stub> {
    private QuizSyncServiceBlockingV2Stub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected QuizSyncServiceBlockingV2Stub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new QuizSyncServiceBlockingV2Stub(channel, callOptions);
    }

    /**
     */
    public com.google.protobuf.Empty createQuizExercise(com.codecampus.quiz.grpc.CreateQuizExerciseRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getCreateQuizExerciseMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.google.protobuf.Empty addQuizDetail(com.codecampus.quiz.grpc.AddQuizDetailRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getAddQuizDetailMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.google.protobuf.Empty addQuestion(com.codecampus.quiz.grpc.AddQuestionRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getAddQuestionMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.google.protobuf.Empty addOption(com.codecampus.quiz.grpc.AddOptionRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getAddOptionMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.google.protobuf.Empty upsertAssignment(com.codecampus.quiz.grpc.UpsertAssignmentRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getUpsertAssignmentMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.google.protobuf.Empty softDeleteExercise(com.codecampus.quiz.grpc.SoftDeleteRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getSoftDeleteExerciseMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.google.protobuf.Empty softDeleteQuestion(com.codecampus.quiz.grpc.SoftDeleteQuestionRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getSoftDeleteQuestionMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.google.protobuf.Empty softDeleteOption(com.codecampus.quiz.grpc.SoftDeleteOptionRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getSoftDeleteOptionMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do limited synchronous rpc calls to service QuizSyncService.
   */
  public static final class QuizSyncServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<QuizSyncServiceBlockingStub> {
    private QuizSyncServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected QuizSyncServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new QuizSyncServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.google.protobuf.Empty createQuizExercise(com.codecampus.quiz.grpc.CreateQuizExerciseRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getCreateQuizExerciseMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.google.protobuf.Empty addQuizDetail(com.codecampus.quiz.grpc.AddQuizDetailRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getAddQuizDetailMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.google.protobuf.Empty addQuestion(com.codecampus.quiz.grpc.AddQuestionRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getAddQuestionMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.google.protobuf.Empty addOption(com.codecampus.quiz.grpc.AddOptionRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getAddOptionMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.google.protobuf.Empty upsertAssignment(com.codecampus.quiz.grpc.UpsertAssignmentRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getUpsertAssignmentMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.google.protobuf.Empty softDeleteExercise(com.codecampus.quiz.grpc.SoftDeleteRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getSoftDeleteExerciseMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.google.protobuf.Empty softDeleteQuestion(com.codecampus.quiz.grpc.SoftDeleteQuestionRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getSoftDeleteQuestionMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.google.protobuf.Empty softDeleteOption(com.codecampus.quiz.grpc.SoftDeleteOptionRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getSoftDeleteOptionMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service QuizSyncService.
   */
  public static final class QuizSyncServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<QuizSyncServiceFutureStub> {
    private QuizSyncServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected QuizSyncServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new QuizSyncServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.protobuf.Empty> createQuizExercise(
        com.codecampus.quiz.grpc.CreateQuizExerciseRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getCreateQuizExerciseMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.protobuf.Empty> addQuizDetail(
        com.codecampus.quiz.grpc.AddQuizDetailRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getAddQuizDetailMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.protobuf.Empty> addQuestion(
        com.codecampus.quiz.grpc.AddQuestionRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getAddQuestionMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.protobuf.Empty> addOption(
        com.codecampus.quiz.grpc.AddOptionRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getAddOptionMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.protobuf.Empty> upsertAssignment(
        com.codecampus.quiz.grpc.UpsertAssignmentRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getUpsertAssignmentMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.protobuf.Empty> softDeleteExercise(
        com.codecampus.quiz.grpc.SoftDeleteRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getSoftDeleteExerciseMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.protobuf.Empty> softDeleteQuestion(
        com.codecampus.quiz.grpc.SoftDeleteQuestionRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getSoftDeleteQuestionMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.protobuf.Empty> softDeleteOption(
        com.codecampus.quiz.grpc.SoftDeleteOptionRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getSoftDeleteOptionMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_CREATE_QUIZ_EXERCISE = 0;
  private static final int METHODID_ADD_QUIZ_DETAIL = 1;
  private static final int METHODID_ADD_QUESTION = 2;
  private static final int METHODID_ADD_OPTION = 3;
  private static final int METHODID_UPSERT_ASSIGNMENT = 4;
  private static final int METHODID_SOFT_DELETE_EXERCISE = 5;
  private static final int METHODID_SOFT_DELETE_QUESTION = 6;
  private static final int METHODID_SOFT_DELETE_OPTION = 7;

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
        case METHODID_CREATE_QUIZ_EXERCISE:
          serviceImpl.createQuizExercise((com.codecampus.quiz.grpc.CreateQuizExerciseRequest) request,
              (io.grpc.stub.StreamObserver<com.google.protobuf.Empty>) responseObserver);
          break;
        case METHODID_ADD_QUIZ_DETAIL:
          serviceImpl.addQuizDetail((com.codecampus.quiz.grpc.AddQuizDetailRequest) request,
              (io.grpc.stub.StreamObserver<com.google.protobuf.Empty>) responseObserver);
          break;
        case METHODID_ADD_QUESTION:
          serviceImpl.addQuestion((com.codecampus.quiz.grpc.AddQuestionRequest) request,
              (io.grpc.stub.StreamObserver<com.google.protobuf.Empty>) responseObserver);
          break;
        case METHODID_ADD_OPTION:
          serviceImpl.addOption((com.codecampus.quiz.grpc.AddOptionRequest) request,
              (io.grpc.stub.StreamObserver<com.google.protobuf.Empty>) responseObserver);
          break;
        case METHODID_UPSERT_ASSIGNMENT:
          serviceImpl.upsertAssignment((com.codecampus.quiz.grpc.UpsertAssignmentRequest) request,
              (io.grpc.stub.StreamObserver<com.google.protobuf.Empty>) responseObserver);
          break;
        case METHODID_SOFT_DELETE_EXERCISE:
          serviceImpl.softDeleteExercise((com.codecampus.quiz.grpc.SoftDeleteRequest) request,
              (io.grpc.stub.StreamObserver<com.google.protobuf.Empty>) responseObserver);
          break;
        case METHODID_SOFT_DELETE_QUESTION:
          serviceImpl.softDeleteQuestion((com.codecampus.quiz.grpc.SoftDeleteQuestionRequest) request,
              (io.grpc.stub.StreamObserver<com.google.protobuf.Empty>) responseObserver);
          break;
        case METHODID_SOFT_DELETE_OPTION:
          serviceImpl.softDeleteOption((com.codecampus.quiz.grpc.SoftDeleteOptionRequest) request,
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
          getCreateQuizExerciseMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.codecampus.quiz.grpc.CreateQuizExerciseRequest,
              com.google.protobuf.Empty>(
                service, METHODID_CREATE_QUIZ_EXERCISE)))
        .addMethod(
          getAddQuizDetailMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.codecampus.quiz.grpc.AddQuizDetailRequest,
              com.google.protobuf.Empty>(
                service, METHODID_ADD_QUIZ_DETAIL)))
        .addMethod(
          getAddQuestionMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.codecampus.quiz.grpc.AddQuestionRequest,
              com.google.protobuf.Empty>(
                service, METHODID_ADD_QUESTION)))
        .addMethod(
          getAddOptionMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.codecampus.quiz.grpc.AddOptionRequest,
              com.google.protobuf.Empty>(
                service, METHODID_ADD_OPTION)))
        .addMethod(
          getUpsertAssignmentMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.codecampus.quiz.grpc.UpsertAssignmentRequest,
              com.google.protobuf.Empty>(
                service, METHODID_UPSERT_ASSIGNMENT)))
        .addMethod(
          getSoftDeleteExerciseMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.codecampus.quiz.grpc.SoftDeleteRequest,
              com.google.protobuf.Empty>(
                service, METHODID_SOFT_DELETE_EXERCISE)))
        .addMethod(
          getSoftDeleteQuestionMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.codecampus.quiz.grpc.SoftDeleteQuestionRequest,
              com.google.protobuf.Empty>(
                service, METHODID_SOFT_DELETE_QUESTION)))
        .addMethod(
          getSoftDeleteOptionMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.codecampus.quiz.grpc.SoftDeleteOptionRequest,
              com.google.protobuf.Empty>(
                service, METHODID_SOFT_DELETE_OPTION)))
        .build();
  }

  private static abstract class QuizSyncServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    QuizSyncServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.codecampus.quiz.grpc.QuizProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("QuizSyncService");
    }
  }

  private static final class QuizSyncServiceFileDescriptorSupplier
      extends QuizSyncServiceBaseDescriptorSupplier {
    QuizSyncServiceFileDescriptorSupplier() {}
  }

  private static final class QuizSyncServiceMethodDescriptorSupplier
      extends QuizSyncServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    QuizSyncServiceMethodDescriptorSupplier(java.lang.String methodName) {
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
      synchronized (QuizSyncServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new QuizSyncServiceFileDescriptorSupplier())
              .addMethod(getCreateQuizExerciseMethod())
              .addMethod(getAddQuizDetailMethod())
              .addMethod(getAddQuestionMethod())
              .addMethod(getAddOptionMethod())
              .addMethod(getUpsertAssignmentMethod())
              .addMethod(getSoftDeleteExerciseMethod())
              .addMethod(getSoftDeleteQuestionMethod())
              .addMethod(getSoftDeleteOptionMethod())
              .build();
        }
      }
    }
    return result;
  }
}
