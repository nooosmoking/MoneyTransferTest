package com.example.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SignupRequest extends Request {
    private String password;

    public SignupRequest(String json) throws JsonProcessingException {
        super();
        ObjectMapper mapper = new ObjectMapper();
        SignupRequest signupRequest = mapper.readValue(json, SignupRequest.class);
        this.setLogin(signupRequest.getLogin());
        this.password = signupRequest.getPassword();
    }
}
