package com.codecampus.identity.dto.data;

import java.util.List;
import lombok.Value;

@Value
public class BulkImportResult {
  int total;
  int created;
  int skipped;
  List<String> errors;
}
