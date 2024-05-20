package com.example.controllers;

import com.example.models.SigninRequest;
import com.example.models.SignupRequest;
import com.example.models.TransferRequest;

import java.io.DataOutputStream;
import java.io.IOException;

public interface BankController {
    void signup(SignupRequest request,  DataOutputStream out) throws IOException;
    void signin(SigninRequest request, DataOutputStream out) throws IOException;
    void transferMoney(TransferRequest request, DataOutputStream out) throws IOException;
    void getBalance(String authToken, DataOutputStream out) throws IOException;
}
