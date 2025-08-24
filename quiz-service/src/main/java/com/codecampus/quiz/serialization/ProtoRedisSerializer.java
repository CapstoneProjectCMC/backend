package com.codecampus.quiz.serialization;

import com.codecampus.quiz.grpc.LoadQuizResponse;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

public class ProtoRedisSerializer
    implements RedisSerializer<LoadQuizResponse> {

  @Override
  public byte[] serialize(LoadQuizResponse value)
      throws SerializationException {
    return (value == null) ? new byte[0] : value.toByteArray();
  }

  @Override
  public LoadQuizResponse deserialize(byte[] bytes)
      throws SerializationException {
    if (bytes == null || bytes.length == 0) {
      return null;
    }

    try {
      return LoadQuizResponse.parseFrom(bytes);
    } catch (Exception e) {
      throw new SerializationException("Cannot parse protobuf", e);
    }
  }
}
