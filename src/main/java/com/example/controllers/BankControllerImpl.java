package com.example.controllers;

import com.example.models.SigninRequest;
import com.example.models.SignupRequest;
import com.example.models.TransferRequest;

import java.io.DataOutputStream;
import java.io.IOException;

public class BankControllerImpl implements BankController{
    @Override
    public void signup(SignupRequest request, DataOutputStream out) throws IOException {

    }

    @Override
    public void signin(SigninRequest request, DataOutputStream out) throws IOException {

    }

    @Override
    public void transferMoney(TransferRequest request, DataOutputStream out) throws IOException {

    }

    @Override
    public void getBalance(DataOutputStream out) throws IOException {

    }
}
