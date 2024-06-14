package com.example.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Response {
    private int status;
    private String statusMessage;
    private String body;
}
