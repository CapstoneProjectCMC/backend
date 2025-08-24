package com.codecampus.profile.constant.file;

import lombok.Getter;

@Getter
public enum FileType {

  Image(0),
  Video(1),
  RegularFile(2),
  Other(3);

  private final int type;

  FileType(
      int type) {
    this.type = type;
  }
}
