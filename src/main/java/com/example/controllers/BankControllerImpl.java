package com.example.controllers;

import com.example.models.SigninRequest;
import com.example.models.SignupRequest;
import com.example.models.TransferRequest;
import com.example.services.AuthService;
import com.example.services.BalanceService;
import com.example.services.TransferService;
import org.springframework.stereotype.Controller;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Controller
public class BankControllerImpl implements BankController{
    private final ExecutorService executorService;
    private final AuthService authService;
    private final TransferService transferService;
    private final BalanceService balanceService;

    public BankControllerImpl(AuthService authService, TransferService transferService, BalanceService balanceService) {
        this.authService = authService;
        this.transferService = transferService;
        this.balanceService = balanceService;
        this.executorService = Executors.newSingleThreadExecutor();
    }

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
    public void getBalance(String authToken, DataOutputStream out) throws IOException {

    }
}
