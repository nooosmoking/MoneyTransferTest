package com.example.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class SignupRequest {
    private String login;
    private String password;

    public SignupRequest(String json) throws JsonProcessingException {
        ObjectMapper mapper =
                new ObjectMapper();
        SignupRequest signupRequest = mapper.readValue(json, SignupRequest.class);
        this.login = signupRequest.getLogin();
        this.password = signupRequest.getPassword();
    }
}
