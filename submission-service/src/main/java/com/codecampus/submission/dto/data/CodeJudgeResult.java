package com.codecampus.submission.dto.data;

public record CodeJudgeResult(
    boolean passed,
    String output,
    String error,
    int timeMs,
    int memoryKb)
{
}