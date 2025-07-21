package com.codecampus.ai.constant.exercise;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public enum Difficulty {
    EASY(1), MEDIUM(2), HARD(3);
    int code;
}
