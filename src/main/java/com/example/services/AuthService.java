package com.example.services;

import com.example.exceptions.NoSuchUserException;
import com.example.exceptions.UserAlreadyExistsException;
import com.example.models.SigninRequest;
import com.example.models.SignupRequest;

public interface AuthService {
    void signUp(SigninRequest signinRequest) throws UserAlreadyExistsException;

    void signIn(SignupRequest signupRequest) throws NoSuchUserException;

}
