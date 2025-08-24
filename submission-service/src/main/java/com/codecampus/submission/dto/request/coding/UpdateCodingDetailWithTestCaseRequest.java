package com.codecampus.submission.dto.request.coding;

import java.util.List;
import java.util.Set;

public record UpdateCodingDetailWithTestCaseRequest(
    String topic,
    Set<String> allowedLanguages,
    String input,
    String output,
    String constraintText,
    Integer timeLimit,
    Integer memoryLimit,
    Integer maxSubmissions,
    String codeTemplate,
    String solution,
    List<TestCasePatchDto> testCases) {
}