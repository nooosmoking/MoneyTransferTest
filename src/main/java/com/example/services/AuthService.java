package com.example.services;

import com.example.exceptions.NoSuchUserException;
import com.example.exceptions.UserAlreadyExistsException;
import com.example.models.SigninRequest;
import com.example.models.SignupRequest;

public interface AuthService {
    void signUp(SignupRequest signinRequest) throws UserAlreadyExistsException;

    void signIn(SigninRequest signupRequest) throws NoSuchUserException;

}
