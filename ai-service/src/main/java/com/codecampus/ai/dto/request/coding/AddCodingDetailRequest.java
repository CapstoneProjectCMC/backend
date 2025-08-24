package com.codecampus.ai.dto.request.coding;

import java.util.List;
import java.util.Set;

public record AddCodingDetailRequest(
        String topic,
        Set<String> allowedLanguages,
        String input,
        String output,
        String constraintText,
        Integer timeLimit,
        Integer memoryLimit,
        Integer maxSubmissions,
        String codeTemplate,
        List<TestCaseDto> testCases,
        String solution
) {
}