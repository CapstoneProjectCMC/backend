package com.codecampus.coding.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class CodeResult {
    boolean passed;
    int runtimeMs;
    int memoryMb;
    String output;
    String error;
}
