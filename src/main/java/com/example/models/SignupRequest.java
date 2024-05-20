package com.example.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class SignupRequest {
    private String login;
    private String password;
}
