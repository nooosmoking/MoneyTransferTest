package com.example.controllers;

import com.example.exceptions.NotEnoughMoneyException;
import com.example.exceptions.UserAlreadyExistsException;
import com.example.models.Request;
import com.example.models.SigninRequest;
import com.example.models.SignupRequest;
import com.example.models.TransferRequest;
import org.springframework.security.core.AuthenticationException;

import java.io.DataOutputStream;
import java.io.IOException;

public interface BankController {
    void signup(SignupRequest request,  DataOutputStream out) throws UserAlreadyExistsException, IOException;
    void signin(SigninRequest request, DataOutputStream out) throws AuthenticationException, IOException;
    void transferMoney(TransferRequest request, DataOutputStream out) throws NotEnoughMoneyException, IOException;
    void getBalance(Request request, DataOutputStream out) ;
}
