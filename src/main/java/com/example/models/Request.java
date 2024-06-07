package com.example.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
public class Request {
     @JsonIgnore
    private Map<String, String> headers;
    private String login;

    public Request(Map<String, String> headers) {
        this.headers = headers;
    }
}
