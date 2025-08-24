package com.codecampus.coding.serialization;

import com.codecampus.coding.grpc.LoadCodingResponse;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

public class ProtoRedisSerializer
    implements RedisSerializer<LoadCodingResponse> {

  @Override
  public byte[] serialize(LoadCodingResponse value)
      throws SerializationException {
    return (value == null) ? new byte[0] : value.toByteArray();
  }

  @Override
  public LoadCodingResponse deserialize(byte[] bytes)
      throws SerializationException {
    if (bytes == null || bytes.length == 0) {
      return null;
    }

    try {
      return LoadCodingResponse.parseFrom(bytes);
    } catch (Exception e) {
      throw new SerializationException("Cannot parse protobuf", e);
    }
  }
}
