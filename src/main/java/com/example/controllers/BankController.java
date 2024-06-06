package com.example.controllers;

import com.example.models.Request;
import com.example.models.SigninRequest;
import com.example.models.SignupRequest;
import com.example.models.TransferRequest;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

public interface BankController {
    void signup(SignupRequest request,  DataOutputStream out) ;
    void signin(SigninRequest request, DataOutputStream out) ;
    void transferMoney(TransferRequest request, DataOutputStream out) ;
    void getBalance(Request request, DataOutputStream out) ;
}
