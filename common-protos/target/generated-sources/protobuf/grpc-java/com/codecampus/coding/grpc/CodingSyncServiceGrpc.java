package com.codecampus.coding.grpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.72.0)",
    comments = "Source: coding.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class CodingSyncServiceGrpc {

  private CodingSyncServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "coding.CodingSyncService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.codecampus.coding.grpc.CreateCodingExerciseRequest,
      com.google.protobuf.Empty> getCreateCodingExerciseMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "CreateCodingExercise",
      requestType = com.codecampus.coding.grpc.CreateCodingExerciseRequest.class,
      responseType = com.google.protobuf.Empty.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.codecampus.coding.grpc.CreateCodingExerciseRequest,
      com.google.protobuf.Empty> getCreateCodingExerciseMethod() {
    io.grpc.MethodDescriptor<com.codecampus.coding.grpc.CreateCodingExerciseRequest, com.google.protobuf.Empty> getCreateCodingExerciseMethod;
    if ((getCreateCodingExerciseMethod = CodingSyncServiceGrpc.getCreateCodingExerciseMethod) == null) {
      synchronized (CodingSyncServiceGrpc.class) {
        if ((getCreateCodingExerciseMethod = CodingSyncServiceGrpc.getCreateCodingExerciseMethod) == null) {
          CodingSyncServiceGrpc.getCreateCodingExerciseMethod = getCreateCodingExerciseMethod =
              io.grpc.MethodDescriptor.<com.codecampus.coding.grpc.CreateCodingExerciseRequest, com.google.protobuf.Empty>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "CreateCodingExercise"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.codecampus.coding.grpc.CreateCodingExerciseRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.protobuf.Empty.getDefaultInstance()))
              .setSchemaDescriptor(new CodingSyncServiceMethodDescriptorSupplier("CreateCodingExercise"))
              .build();
        }
      }
    }
    return getCreateCodingExerciseMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.codecampus.coding.grpc.AddCodingDetailRequest,
      com.google.protobuf.Empty> getAddCodingDetailMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "AddCodingDetail",
      requestType = com.codecampus.coding.grpc.AddCodingDetailRequest.class,
      responseType = com.google.protobuf.Empty.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.codecampus.coding.grpc.AddCodingDetailRequest,
      com.google.protobuf.Empty> getAddCodingDetailMethod() {
    io.grpc.MethodDescriptor<com.codecampus.coding.grpc.AddCodingDetailRequest, com.google.protobuf.Empty> getAddCodingDetailMethod;
    if ((getAddCodingDetailMethod = CodingSyncServiceGrpc.getAddCodingDetailMethod) == null) {
      synchronized (CodingSyncServiceGrpc.class) {
        if ((getAddCodingDetailMethod = CodingSyncServiceGrpc.getAddCodingDetailMethod) == null) {
          CodingSyncServiceGrpc.getAddCodingDetailMethod = getAddCodingDetailMethod =
              io.grpc.MethodDescriptor.<com.codecampus.coding.grpc.AddCodingDetailRequest, com.google.protobuf.Empty>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "AddCodingDetail"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.codecampus.coding.grpc.AddCodingDetailRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.protobuf.Empty.getDefaultInstance()))
              .setSchemaDescriptor(new CodingSyncServiceMethodDescriptorSupplier("AddCodingDetail"))
              .build();
        }
      }
    }
    return getAddCodingDetailMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.codecampus.coding.grpc.AddTestCaseRequest,
      com.google.protobuf.Empty> getAddTestCaseMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "AddTestCase",
      requestType = com.codecampus.coding.grpc.AddTestCaseRequest.class,
      responseType = com.google.protobuf.Empty.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.codecampus.coding.grpc.AddTestCaseRequest,
      com.google.protobuf.Empty> getAddTestCaseMethod() {
    io.grpc.MethodDescriptor<com.codecampus.coding.grpc.AddTestCaseRequest, com.google.protobuf.Empty> getAddTestCaseMethod;
    if ((getAddTestCaseMethod = CodingSyncServiceGrpc.getAddTestCaseMethod) == null) {
      synchronized (CodingSyncServiceGrpc.class) {
        if ((getAddTestCaseMethod = CodingSyncServiceGrpc.getAddTestCaseMethod) == null) {
          CodingSyncServiceGrpc.getAddTestCaseMethod = getAddTestCaseMethod =
              io.grpc.MethodDescriptor.<com.codecampus.coding.grpc.AddTestCaseRequest, com.google.protobuf.Empty>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "AddTestCase"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.codecampus.coding.grpc.AddTestCaseRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.protobuf.Empty.getDefaultInstance()))
              .setSchemaDescriptor(new CodingSyncServiceMethodDescriptorSupplier("AddTestCase"))
              .build();
        }
      }
    }
    return getAddTestCaseMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.codecampus.coding.grpc.SoftDeleteRequest,
      com.google.protobuf.Empty> getSoftDeleteExerciseMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "SoftDeleteExercise",
      requestType = com.codecampus.coding.grpc.SoftDeleteRequest.class,
      responseType = com.google.protobuf.Empty.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.codecampus.coding.grpc.SoftDeleteRequest,
      com.google.protobuf.Empty> getSoftDeleteExerciseMethod() {
    io.grpc.MethodDescriptor<com.codecampus.coding.grpc.SoftDeleteRequest, com.google.protobuf.Empty> getSoftDeleteExerciseMethod;
    if ((getSoftDeleteExerciseMethod = CodingSyncServiceGrpc.getSoftDeleteExerciseMethod) == null) {
      synchronized (CodingSyncServiceGrpc.class) {
        if ((getSoftDeleteExerciseMethod = CodingSyncServiceGrpc.getSoftDeleteExerciseMethod) == null) {
          CodingSyncServiceGrpc.getSoftDeleteExerciseMethod = getSoftDeleteExerciseMethod =
              io.grpc.MethodDescriptor.<com.codecampus.coding.grpc.SoftDeleteRequest, com.google.protobuf.Empty>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "SoftDeleteExercise"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.codecampus.coding.grpc.SoftDeleteRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.protobuf.Empty.getDefaultInstance()))
              .setSchemaDescriptor(new CodingSyncServiceMethodDescriptorSupplier("SoftDeleteExercise"))
              .build();
        }
      }
    }
    return getSoftDeleteExerciseMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.codecampus.coding.grpc.SoftDeleteTestCaseRequest,
      com.google.protobuf.Empty> getSoftDeleteTestCaseMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "SoftDeleteTestCase",
      requestType = com.codecampus.coding.grpc.SoftDeleteTestCaseRequest.class,
      responseType = com.google.protobuf.Empty.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.codecampus.coding.grpc.SoftDeleteTestCaseRequest,
      com.google.protobuf.Empty> getSoftDeleteTestCaseMethod() {
    io.grpc.MethodDescriptor<com.codecampus.coding.grpc.SoftDeleteTestCaseRequest, com.google.protobuf.Empty> getSoftDeleteTestCaseMethod;
    if ((getSoftDeleteTestCaseMethod = CodingSyncServiceGrpc.getSoftDeleteTestCaseMethod) == null) {
      synchronized (CodingSyncServiceGrpc.class) {
        if ((getSoftDeleteTestCaseMethod = CodingSyncServiceGrpc.getSoftDeleteTestCaseMethod) == null) {
          CodingSyncServiceGrpc.getSoftDeleteTestCaseMethod = getSoftDeleteTestCaseMethod =
              io.grpc.MethodDescriptor.<com.codecampus.coding.grpc.SoftDeleteTestCaseRequest, com.google.protobuf.Empty>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "SoftDeleteTestCase"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.codecampus.coding.grpc.SoftDeleteTestCaseRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.protobuf.Empty.getDefaultInstance()))
              .setSchemaDescriptor(new CodingSyncServiceMethodDescriptorSupplier("SoftDeleteTestCase"))
              .build();
        }
      }
    }
    return getSoftDeleteTestCaseMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.codecampus.coding.grpc.UpsertAssignmentRequest,
      com.google.protobuf.Empty> getUpsertAssignmentMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "UpsertAssignment",
      requestType = com.codecampus.coding.grpc.UpsertAssignmentRequest.class,
      responseType = com.google.protobuf.Empty.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.codecampus.coding.grpc.UpsertAssignmentRequest,
      com.google.protobuf.Empty> getUpsertAssignmentMethod() {
    io.grpc.MethodDescriptor<com.codecampus.coding.grpc.UpsertAssignmentRequest, com.google.protobuf.Empty> getUpsertAssignmentMethod;
    if ((getUpsertAssignmentMethod = CodingSyncServiceGrpc.getUpsertAssignmentMethod) == null) {
      synchronized (CodingSyncServiceGrpc.class) {
        if ((getUpsertAssignmentMethod = CodingSyncServiceGrpc.getUpsertAssignmentMethod) == null) {
          CodingSyncServiceGrpc.getUpsertAssignmentMethod = getUpsertAssignmentMethod =
              io.grpc.MethodDescriptor.<com.codecampus.coding.grpc.UpsertAssignmentRequest, com.google.protobuf.Empty>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "UpsertAssignment"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.codecampus.coding.grpc.UpsertAssignmentRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.protobuf.Empty.getDefaultInstance()))
              .setSchemaDescriptor(new CodingSyncServiceMethodDescriptorSupplier("UpsertAssignment"))
              .build();
        }
      }
    }
    return getUpsertAssignmentMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static CodingSyncServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<CodingSyncServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<CodingSyncServiceStub>() {
        @java.lang.Override
        public CodingSyncServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new CodingSyncServiceStub(channel, callOptions);
        }
      };
    return CodingSyncServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports all types of calls on the service
   */
  public static CodingSyncServiceBlockingV2Stub newBlockingV2Stub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<CodingSyncServiceBlockingV2Stub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<CodingSyncServiceBlockingV2Stub>() {
        @java.lang.Override
        public CodingSyncServiceBlockingV2Stub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new CodingSyncServiceBlockingV2Stub(channel, callOptions);
        }
      };
    return CodingSyncServiceBlockingV2Stub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static CodingSyncServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<CodingSyncServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<CodingSyncServiceBlockingStub>() {
        @java.lang.Override
        public CodingSyncServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new CodingSyncServiceBlockingStub(channel, callOptions);
        }
      };
    return CodingSyncServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static CodingSyncServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<CodingSyncServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<CodingSyncServiceFutureStub>() {
        @java.lang.Override
        public CodingSyncServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new CodingSyncServiceFutureStub(channel, callOptions);
        }
      };
    return CodingSyncServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     */
    default void createCodingExercise(com.codecampus.coding.grpc.CreateCodingExerciseRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getCreateCodingExerciseMethod(), responseObserver);
    }

    /**
     */
    default void addCodingDetail(com.codecampus.coding.grpc.AddCodingDetailRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getAddCodingDetailMethod(), responseObserver);
    }

    /**
     */
    default void addTestCase(com.codecampus.coding.grpc.AddTestCaseRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getAddTestCaseMethod(), responseObserver);
    }

    /**
     */
    default void softDeleteExercise(com.codecampus.coding.grpc.SoftDeleteRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getSoftDeleteExerciseMethod(), responseObserver);
    }

    /**
     */
    default void softDeleteTestCase(com.codecampus.coding.grpc.SoftDeleteTestCaseRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getSoftDeleteTestCaseMethod(), responseObserver);
    }

    /**
     */
    default void upsertAssignment(com.codecampus.coding.grpc.UpsertAssignmentRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getUpsertAssignmentMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service CodingSyncService.
   */
  public static abstract class CodingSyncServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return CodingSyncServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service CodingSyncService.
   */
  public static final class CodingSyncServiceStub
      extends io.grpc.stub.AbstractAsyncStub<CodingSyncServiceStub> {
    private CodingSyncServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected CodingSyncServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new CodingSyncServiceStub(channel, callOptions);
    }

    /**
     */
    public void createCodingExercise(com.codecampus.coding.grpc.CreateCodingExerciseRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getCreateCodingExerciseMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void addCodingDetail(com.codecampus.coding.grpc.AddCodingDetailRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getAddCodingDetailMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void addTestCase(com.codecampus.coding.grpc.AddTestCaseRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getAddTestCaseMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void softDeleteExercise(com.codecampus.coding.grpc.SoftDeleteRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getSoftDeleteExerciseMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void softDeleteTestCase(com.codecampus.coding.grpc.SoftDeleteTestCaseRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getSoftDeleteTestCaseMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void upsertAssignment(com.codecampus.coding.grpc.UpsertAssignmentRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getUpsertAssignmentMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service CodingSyncService.
   */
  public static final class CodingSyncServiceBlockingV2Stub
      extends io.grpc.stub.AbstractBlockingStub<CodingSyncServiceBlockingV2Stub> {
    private CodingSyncServiceBlockingV2Stub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected CodingSyncServiceBlockingV2Stub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new CodingSyncServiceBlockingV2Stub(channel, callOptions);
    }

    /**
     */
    public com.google.protobuf.Empty createCodingExercise(com.codecampus.coding.grpc.CreateCodingExerciseRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getCreateCodingExerciseMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.google.protobuf.Empty addCodingDetail(com.codecampus.coding.grpc.AddCodingDetailRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getAddCodingDetailMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.google.protobuf.Empty addTestCase(com.codecampus.coding.grpc.AddTestCaseRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getAddTestCaseMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.google.protobuf.Empty softDeleteExercise(com.codecampus.coding.grpc.SoftDeleteRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getSoftDeleteExerciseMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.google.protobuf.Empty softDeleteTestCase(com.codecampus.coding.grpc.SoftDeleteTestCaseRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getSoftDeleteTestCaseMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.google.protobuf.Empty upsertAssignment(com.codecampus.coding.grpc.UpsertAssignmentRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getUpsertAssignmentMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do limited synchronous rpc calls to service CodingSyncService.
   */
  public static final class CodingSyncServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<CodingSyncServiceBlockingStub> {
    private CodingSyncServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected CodingSyncServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new CodingSyncServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.google.protobuf.Empty createCodingExercise(com.codecampus.coding.grpc.CreateCodingExerciseRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getCreateCodingExerciseMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.google.protobuf.Empty addCodingDetail(com.codecampus.coding.grpc.AddCodingDetailRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getAddCodingDetailMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.google.protobuf.Empty addTestCase(com.codecampus.coding.grpc.AddTestCaseRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getAddTestCaseMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.google.protobuf.Empty softDeleteExercise(com.codecampus.coding.grpc.SoftDeleteRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getSoftDeleteExerciseMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.google.protobuf.Empty softDeleteTestCase(com.codecampus.coding.grpc.SoftDeleteTestCaseRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getSoftDeleteTestCaseMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.google.protobuf.Empty upsertAssignment(com.codecampus.coding.grpc.UpsertAssignmentRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getUpsertAssignmentMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service CodingSyncService.
   */
  public static final class CodingSyncServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<CodingSyncServiceFutureStub> {
    private CodingSyncServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected CodingSyncServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new CodingSyncServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.protobuf.Empty> createCodingExercise(
        com.codecampus.coding.grpc.CreateCodingExerciseRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getCreateCodingExerciseMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.protobuf.Empty> addCodingDetail(
        com.codecampus.coding.grpc.AddCodingDetailRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getAddCodingDetailMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.protobuf.Empty> addTestCase(
        com.codecampus.coding.grpc.AddTestCaseRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getAddTestCaseMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.protobuf.Empty> softDeleteExercise(
        com.codecampus.coding.grpc.SoftDeleteRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getSoftDeleteExerciseMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.protobuf.Empty> softDeleteTestCase(
        com.codecampus.coding.grpc.SoftDeleteTestCaseRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getSoftDeleteTestCaseMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.protobuf.Empty> upsertAssignment(
        com.codecampus.coding.grpc.UpsertAssignmentRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getUpsertAssignmentMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_CREATE_CODING_EXERCISE = 0;
  private static final int METHODID_ADD_CODING_DETAIL = 1;
  private static final int METHODID_ADD_TEST_CASE = 2;
  private static final int METHODID_SOFT_DELETE_EXERCISE = 3;
  private static final int METHODID_SOFT_DELETE_TEST_CASE = 4;
  private static final int METHODID_UPSERT_ASSIGNMENT = 5;

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
        case METHODID_CREATE_CODING_EXERCISE:
          serviceImpl.createCodingExercise((com.codecampus.coding.grpc.CreateCodingExerciseRequest) request,
              (io.grpc.stub.StreamObserver<com.google.protobuf.Empty>) responseObserver);
          break;
        case METHODID_ADD_CODING_DETAIL:
          serviceImpl.addCodingDetail((com.codecampus.coding.grpc.AddCodingDetailRequest) request,
              (io.grpc.stub.StreamObserver<com.google.protobuf.Empty>) responseObserver);
          break;
        case METHODID_ADD_TEST_CASE:
          serviceImpl.addTestCase((com.codecampus.coding.grpc.AddTestCaseRequest) request,
              (io.grpc.stub.StreamObserver<com.google.protobuf.Empty>) responseObserver);
          break;
        case METHODID_SOFT_DELETE_EXERCISE:
          serviceImpl.softDeleteExercise((com.codecampus.coding.grpc.SoftDeleteRequest) request,
              (io.grpc.stub.StreamObserver<com.google.protobuf.Empty>) responseObserver);
          break;
        case METHODID_SOFT_DELETE_TEST_CASE:
          serviceImpl.softDeleteTestCase((com.codecampus.coding.grpc.SoftDeleteTestCaseRequest) request,
              (io.grpc.stub.StreamObserver<com.google.protobuf.Empty>) responseObserver);
          break;
        case METHODID_UPSERT_ASSIGNMENT:
          serviceImpl.upsertAssignment((com.codecampus.coding.grpc.UpsertAssignmentRequest) request,
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
          getCreateCodingExerciseMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.codecampus.coding.grpc.CreateCodingExerciseRequest,
              com.google.protobuf.Empty>(
                service, METHODID_CREATE_CODING_EXERCISE)))
        .addMethod(
          getAddCodingDetailMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.codecampus.coding.grpc.AddCodingDetailRequest,
              com.google.protobuf.Empty>(
                service, METHODID_ADD_CODING_DETAIL)))
        .addMethod(
          getAddTestCaseMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.codecampus.coding.grpc.AddTestCaseRequest,
              com.google.protobuf.Empty>(
                service, METHODID_ADD_TEST_CASE)))
        .addMethod(
          getSoftDeleteExerciseMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.codecampus.coding.grpc.SoftDeleteRequest,
              com.google.protobuf.Empty>(
                service, METHODID_SOFT_DELETE_EXERCISE)))
        .addMethod(
          getSoftDeleteTestCaseMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.codecampus.coding.grpc.SoftDeleteTestCaseRequest,
              com.google.protobuf.Empty>(
                service, METHODID_SOFT_DELETE_TEST_CASE)))
        .addMethod(
          getUpsertAssignmentMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.codecampus.coding.grpc.UpsertAssignmentRequest,
              com.google.protobuf.Empty>(
                service, METHODID_UPSERT_ASSIGNMENT)))
        .build();
  }

  private static abstract class CodingSyncServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    CodingSyncServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.codecampus.coding.grpc.CodingProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("CodingSyncService");
    }
  }

  private static final class CodingSyncServiceFileDescriptorSupplier
      extends CodingSyncServiceBaseDescriptorSupplier {
    CodingSyncServiceFileDescriptorSupplier() {}
  }

  private static final class CodingSyncServiceMethodDescriptorSupplier
      extends CodingSyncServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    CodingSyncServiceMethodDescriptorSupplier(java.lang.String methodName) {
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
      synchronized (CodingSyncServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new CodingSyncServiceFileDescriptorSupplier())
              .addMethod(getCreateCodingExerciseMethod())
              .addMethod(getAddCodingDetailMethod())
              .addMethod(getAddTestCaseMethod())
              .addMethod(getSoftDeleteExerciseMethod())
              .addMethod(getSoftDeleteTestCaseMethod())
              .addMethod(getUpsertAssignmentMethod())
              .build();
        }
      }
    }
    return result;
  }
}
