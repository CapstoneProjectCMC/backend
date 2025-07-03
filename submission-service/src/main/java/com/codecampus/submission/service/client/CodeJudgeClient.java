package com.codecampus.submission.service.client;

import com.codecampus.submission.dto.data.CodeJudgeResult;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class CodeJudgeClient
{
  public CodeJudgeResult run(
      String lang,
      String src,
      String input,
      String expected,
      int tl, int ml)
  {
    String out = exec(src, input);
    boolean ok = Objects.equals(out.trim(), expected.trim());
    return new CodeJudgeResult(ok, out, null, 40, 12_000);
  }

  public String exec(String code, String input)
  {
    return input;
  }
}