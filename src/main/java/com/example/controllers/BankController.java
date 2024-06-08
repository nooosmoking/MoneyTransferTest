package com.example.controllers;

import com.example.exceptions.NotEnoughMoneyException;
import com.example.exceptions.UserAlreadyExistsException;
import com.example.models.*;
import org.springframework.security.core.AuthenticationException;

import java.io.DataOutputStream;
import java.io.IOException;

public interface BankController {
    Response signup(SignupRequest request) throws UserAlreadyExistsException, IOException;
    Response signin(SigninRequest request) throws AuthenticationException, IOException;
    Response transferMoney(TransferRequest request) throws NotEnoughMoneyException, IOException;
    Response getBalance(Request request) throws IOException;
}
