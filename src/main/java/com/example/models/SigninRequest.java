package com.example.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class SigninRequest {
    private String login;
    private String password;
}
