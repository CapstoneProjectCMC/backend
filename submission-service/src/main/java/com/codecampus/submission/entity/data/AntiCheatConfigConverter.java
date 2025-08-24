package com.codecampus.submission.entity.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class AntiCheatConfigConverter
    implements AttributeConverter<AntiCheatConfig, String> {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public String convertToDatabaseColumn(
      AntiCheatConfig antiCheatConfig) {

    if (antiCheatConfig == null) {
      return null;
    }

    try {
      return objectMapper.writeValueAsString(antiCheatConfig);
    } catch (JsonProcessingException exception) {
      throw new IllegalArgumentException(exception);
    }
  }

  @Override
  public AntiCheatConfig convertToEntityAttribute(
      String jsonString) {

    if (jsonString == null || jsonString.isBlank()) {
      return null;
    }

    try {
      return objectMapper.readValue(jsonString, AntiCheatConfig.class);
    } catch (JsonProcessingException exception) {
      throw new IllegalArgumentException(exception);
    }
  }
}
