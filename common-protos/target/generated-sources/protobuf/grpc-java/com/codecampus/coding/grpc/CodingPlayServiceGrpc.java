package com.codecampus.coding.grpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.72.0)",
    comments = "Source: coding.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class CodingPlayServiceGrpc {

  private CodingPlayServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "coding.CodingPlayService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.codecampus.coding.grpc.SubmitCodeRequest,
      com.codecampus.coding.grpc.SubmitCodeResponse> getSubmitCodeMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "SubmitCode",
      requestType = com.codecampus.coding.grpc.SubmitCodeRequest.class,
      responseType = com.codecampus.coding.grpc.SubmitCodeResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.codecampus.coding.grpc.SubmitCodeRequest,
      com.codecampus.coding.grpc.SubmitCodeResponse> getSubmitCodeMethod() {
    io.grpc.MethodDescriptor<com.codecampus.coding.grpc.SubmitCodeRequest, com.codecampus.coding.grpc.SubmitCodeResponse> getSubmitCodeMethod;
    if ((getSubmitCodeMethod = CodingPlayServiceGrpc.getSubmitCodeMethod) == null) {
      synchronized (CodingPlayServiceGrpc.class) {
        if ((getSubmitCodeMethod = CodingPlayServiceGrpc.getSubmitCodeMethod) == null) {
          CodingPlayServiceGrpc.getSubmitCodeMethod = getSubmitCodeMethod =
              io.grpc.MethodDescriptor.<com.codecampus.coding.grpc.SubmitCodeRequest, com.codecampus.coding.grpc.SubmitCodeResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "SubmitCode"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.codecampus.coding.grpc.SubmitCodeRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.codecampus.coding.grpc.SubmitCodeResponse.getDefaultInstance()))
              .setSchemaDescriptor(new CodingPlayServiceMethodDescriptorSupplier("SubmitCode"))
              .build();
        }
      }
    }
    return getSubmitCodeMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.codecampus.coding.grpc.LoadCodingRequest,
      com.codecampus.coding.grpc.LoadCodingResponse> getLoadCodingMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "LoadCoding",
      requestType = com.codecampus.coding.grpc.LoadCodingRequest.class,
      responseType = com.codecampus.coding.grpc.LoadCodingResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.codecampus.coding.grpc.LoadCodingRequest,
      com.codecampus.coding.grpc.LoadCodingResponse> getLoadCodingMethod() {
    io.grpc.MethodDescriptor<com.codecampus.coding.grpc.LoadCodingRequest, com.codecampus.coding.grpc.LoadCodingResponse> getLoadCodingMethod;
    if ((getLoadCodingMethod = CodingPlayServiceGrpc.getLoadCodingMethod) == null) {
      synchronized (CodingPlayServiceGrpc.class) {
        if ((getLoadCodingMethod = CodingPlayServiceGrpc.getLoadCodingMethod) == null) {
          CodingPlayServiceGrpc.getLoadCodingMethod = getLoadCodingMethod =
              io.grpc.MethodDescriptor.<com.codecampus.coding.grpc.LoadCodingRequest, com.codecampus.coding.grpc.LoadCodingResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "LoadCoding"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.codecampus.coding.grpc.LoadCodingRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.codecampus.coding.grpc.LoadCodingResponse.getDefaultInstance()))
              .setSchemaDescriptor(new CodingPlayServiceMethodDescriptorSupplier("LoadCoding"))
              .build();
        }
      }
    }
    return getLoadCodingMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static CodingPlayServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<CodingPlayServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<CodingPlayServiceStub>() {
        @java.lang.Override
        public CodingPlayServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new CodingPlayServiceStub(channel, callOptions);
        }
      };
    return CodingPlayServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports all types of calls on the service
   */
  public static CodingPlayServiceBlockingV2Stub newBlockingV2Stub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<CodingPlayServiceBlockingV2Stub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<CodingPlayServiceBlockingV2Stub>() {
        @java.lang.Override
        public CodingPlayServiceBlockingV2Stub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new CodingPlayServiceBlockingV2Stub(channel, callOptions);
        }
      };
    return CodingPlayServiceBlockingV2Stub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static CodingPlayServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<CodingPlayServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<CodingPlayServiceBlockingStub>() {
        @java.lang.Override
        public CodingPlayServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new CodingPlayServiceBlockingStub(channel, callOptions);
        }
      };
    return CodingPlayServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static CodingPlayServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<CodingPlayServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<CodingPlayServiceFutureStub>() {
        @java.lang.Override
        public CodingPlayServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new CodingPlayServiceFutureStub(channel, callOptions);
        }
      };
    return CodingPlayServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     */
    default void submitCode(com.codecampus.coding.grpc.SubmitCodeRequest request,
        io.grpc.stub.StreamObserver<com.codecampus.coding.grpc.SubmitCodeResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getSubmitCodeMethod(), responseObserver);
    }

    /**
     */
    default void loadCoding(com.codecampus.coding.grpc.LoadCodingRequest request,
        io.grpc.stub.StreamObserver<com.codecampus.coding.grpc.LoadCodingResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getLoadCodingMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service CodingPlayService.
   */
  public static abstract class CodingPlayServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return CodingPlayServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service CodingPlayService.
   */
  public static final class CodingPlayServiceStub
      extends io.grpc.stub.AbstractAsyncStub<CodingPlayServiceStub> {
    private CodingPlayServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected CodingPlayServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new CodingPlayServiceStub(channel, callOptions);
    }

    /**
     */
    public void submitCode(com.codecampus.coding.grpc.SubmitCodeRequest request,
        io.grpc.stub.StreamObserver<com.codecampus.coding.grpc.SubmitCodeResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getSubmitCodeMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void loadCoding(com.codecampus.coding.grpc.LoadCodingRequest request,
        io.grpc.stub.StreamObserver<com.codecampus.coding.grpc.LoadCodingResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getLoadCodingMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service CodingPlayService.
   */
  public static final class CodingPlayServiceBlockingV2Stub
      extends io.grpc.stub.AbstractBlockingStub<CodingPlayServiceBlockingV2Stub> {
    private CodingPlayServiceBlockingV2Stub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected CodingPlayServiceBlockingV2Stub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new CodingPlayServiceBlockingV2Stub(channel, callOptions);
    }

    /**
     */
    public com.codecampus.coding.grpc.SubmitCodeResponse submitCode(com.codecampus.coding.grpc.SubmitCodeRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getSubmitCodeMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.codecampus.coding.grpc.LoadCodingResponse loadCoding(com.codecampus.coding.grpc.LoadCodingRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getLoadCodingMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do limited synchronous rpc calls to service CodingPlayService.
   */
  public static final class CodingPlayServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<CodingPlayServiceBlockingStub> {
    private CodingPlayServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected CodingPlayServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new CodingPlayServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.codecampus.coding.grpc.SubmitCodeResponse submitCode(com.codecampus.coding.grpc.SubmitCodeRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getSubmitCodeMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.codecampus.coding.grpc.LoadCodingResponse loadCoding(com.codecampus.coding.grpc.LoadCodingRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getLoadCodingMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service CodingPlayService.
   */
  public static final class CodingPlayServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<CodingPlayServiceFutureStub> {
    private CodingPlayServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected CodingPlayServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new CodingPlayServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.codecampus.coding.grpc.SubmitCodeResponse> submitCode(
        com.codecampus.coding.grpc.SubmitCodeRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getSubmitCodeMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.codecampus.coding.grpc.LoadCodingResponse> loadCoding(
        com.codecampus.coding.grpc.LoadCodingRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getLoadCodingMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_SUBMIT_CODE = 0;
  private static final int METHODID_LOAD_CODING = 1;

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
        case METHODID_SUBMIT_CODE:
          serviceImpl.submitCode((com.codecampus.coding.grpc.SubmitCodeRequest) request,
              (io.grpc.stub.StreamObserver<com.codecampus.coding.grpc.SubmitCodeResponse>) responseObserver);
          break;
        case METHODID_LOAD_CODING:
          serviceImpl.loadCoding((com.codecampus.coding.grpc.LoadCodingRequest) request,
              (io.grpc.stub.StreamObserver<com.codecampus.coding.grpc.LoadCodingResponse>) responseObserver);
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
          getSubmitCodeMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.codecampus.coding.grpc.SubmitCodeRequest,
              com.codecampus.coding.grpc.SubmitCodeResponse>(
                service, METHODID_SUBMIT_CODE)))
        .addMethod(
          getLoadCodingMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.codecampus.coding.grpc.LoadCodingRequest,
              com.codecampus.coding.grpc.LoadCodingResponse>(
                service, METHODID_LOAD_CODING)))
        .build();
  }

  private static abstract class CodingPlayServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    CodingPlayServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.codecampus.coding.grpc.CodingProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("CodingPlayService");
    }
  }

  private static final class CodingPlayServiceFileDescriptorSupplier
      extends CodingPlayServiceBaseDescriptorSupplier {
    CodingPlayServiceFileDescriptorSupplier() {}
  }

  private static final class CodingPlayServiceMethodDescriptorSupplier
      extends CodingPlayServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    CodingPlayServiceMethodDescriptorSupplier(java.lang.String methodName) {
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
      synchronized (CodingPlayServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new CodingPlayServiceFileDescriptorSupplier())
              .addMethod(getSubmitCodeMethod())
              .addMethod(getLoadCodingMethod())
              .build();
        }
      }
    }
    return result;
  }
}
