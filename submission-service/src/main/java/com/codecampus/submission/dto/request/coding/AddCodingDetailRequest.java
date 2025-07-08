package com.codecampus.submission.dto.request.coding;

import java.util.List;
import java.util.Set;

public record AddCodingDetailRequest(
        String topic,
        Set<String> allowedLanguages,
        String input,
        String output,
        String constraintText,
        int timeLimit,
        int memoryLimit,
        int maxSubmissions,
        String codeTemplate,
        List<TestCaseDto> testCases) {
}