package com.example.controllers;

import com.example.controllers.aspects.AuthAspect;
import com.example.controllers.aspects.AuthRequired;
import com.example.exceptions.NoSuchUserException;
import com.example.exceptions.NotEnoughMoneyException;
import com.example.exceptions.UserAlreadyExistsException;
import com.example.models.*;
import com.example.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;

import java.io.IOException;

@Controller
public class BankControllerImpl implements BankController {
    private final AuthService authService;
    private final TransferService transferService;
    private final BalanceService balanceService;
    private boolean isTransferComplete = true;
    private final AuthAspect authAspect;

    @Autowired
    public BankControllerImpl(AuthService authService, TransferService transferService, BalanceService balanceService, AuthAspect authAspect) {
        this.authService = authService;
        this.transferService = transferService;
        this.balanceService = balanceService;
        this.authAspect = authAspect;
    }

    @Override
    public Response signup(SignupRequest request) throws UserAlreadyExistsException, IOException {
        String token = authService.signUp(request);
        return new Response( 200, "OK", "{\"token\":\"" + token + "\"}");

    }

    @Override
    public Response signin(SigninRequest request) throws AuthenticationException, NoSuchUserException, IOException{
        String token = authService.signIn(request);
        return new Response(  200, "OK", "{\"token\":\"" + token + "\"}");
    }

    @Override
    @AuthRequired
    public Response transferMoney(TransferRequest request) throws NotEnoughMoneyException, NoSuchUserException, IllegalArgumentException, IOException {
        isTransferComplete = false;
        try {
            transferService.transfer(request);
            return new Response( 200, "OK", "");
        } finally {
            isTransferComplete = true;
        }
    }

    @Override
    @AuthRequired
    public Response getBalance(Request request) throws IOException {
        while (!isTransferComplete) {
            Thread.onSpinWait();
        }
        int balance = balanceService.getBalance(request);
        return new Response(  200, "OK", "{\"balance\":"+balance+"}");
    }

//    private void sendResponse(DataOutputStream out, int status, String statusMessage, String body, boolean includeContentType) throws IOException {
//        StringBuilder response = new StringBuilder("HTTP/1.1 " + status + " " + statusMessage + "\r\n");
//
//        if (includeContentType) {
//            response.append("Content-Type: application/json\r\n");
//            response.append("Content-Length: ").append(body.length()).append("\r\n");
//
//            response.append("\r\n");
//            response.append(body);
//        }
//
//        out.write(response.toString().getBytes());
//    }
}
