package com.codecampus.post.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class AddFileResponseDto {
    private int code;
    private String message;
    private String status;
    private String result;
}
