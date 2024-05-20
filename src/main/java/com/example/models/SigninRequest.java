package com.example.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class SigninRequest {
    private String login;
    private String password;

    public SigninRequest(String json) throws JsonProcessingException {
        ObjectMapper mapper =
                new ObjectMapper();
        SigninRequest signinRequest = mapper.readValue(json, SigninRequest.class);
        this.login = signinRequest.getLogin();
        this.password = signinRequest.getPassword();
    }
}
