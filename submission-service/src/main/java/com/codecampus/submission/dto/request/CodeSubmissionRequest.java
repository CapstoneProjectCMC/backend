package com.codecampus.submission.dto.request;

public record CodeSubmissionRequest(
    String exerciseId,
    String language,
    String sourceCode)
{
}
