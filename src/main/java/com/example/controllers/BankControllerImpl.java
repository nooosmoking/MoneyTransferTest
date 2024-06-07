package com.example.controllers;

import com.example.controllers.annotations.AuthRequired;
import com.example.controllers.aspects.AuthAspect;
import com.example.exceptions.NoSuchUserException;
import com.example.exceptions.NotEnoughMoneyException;
import com.example.exceptions.UserAlreadyExistsException;
import com.example.models.Request;
import com.example.models.SigninRequest;
import com.example.models.SignupRequest;
import com.example.models.TransferRequest;
import com.example.services.AuthServiceImpl;
import com.example.services.BalanceServiceImpl;
import com.example.services.TransferServiceImpl;
import org.springframework.stereotype.Controller;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Controller
public class BankControllerImpl implements BankController {
    private final AuthServiceImpl authService;
    private final TransferServiceImpl transferService;
    private final BalanceServiceImpl balanceService;
    private boolean isTransferComplete = true;
    private final AuthAspect authAspect;

    public BankControllerImpl(AuthServiceImpl authService, TransferServiceImpl transferService, BalanceServiceImpl balanceService, AuthAspect authAspect) {
        this.authService = authService;
        this.transferService = transferService;
        this.balanceService = balanceService;
        this.authAspect = authAspect;
    }

    @Override
    public void signup(SignupRequest request, DataOutputStream out) {
            try {
                try {
                    String token = authService.signUp(request);
                    sendResponse(out, 200, "OK", "{\"token\":\""+token+"\"}", true);
                } catch (UserAlreadyExistsException  ex) {
                    sendResponse(out, 409, "Conflict", "{\"message\": \"" + ex.getMessage() + "\"}", true);
                }
            } catch (IOException ex) {
                System.err.println("Error while sending http response.");
            }
    }

    @Override
    public void signin(SigninRequest request, DataOutputStream out) {
        try {
            try {
                String token = authService.signIn(request);
                sendResponse(out, 200, "OK", "", true);
            } catch (NoSuchUserException  ex) {
                sendResponse(out, 404, "Not found", "{\"message\": \"" + ex.getMessage() + "\"}", true);
            }
        } catch (IOException ex) {
            System.err.println("Error while sending http response.");
        }
    }

    @Override
    @AuthRequired
    public void transferMoney(TransferRequest request, DataOutputStream out) {
        isTransferComplete = false;
        try {
            try {
                transferService.transfer(request);
                sendResponse(out, 200, "OK", "", false);
            } catch (NotEnoughMoneyException | NoSuchUserException | IllegalArgumentException ex) {
                sendResponse(out, 400, "Bad Request", "{\"message\": \"" + ex.getMessage() + "\"}", true);
            }
        } catch (IOException ex) {
            System.err.println("Error while sending http response.");
        }
             isTransferComplete = true;
    }

    @Override
    @AuthRequired
    public void getBalance(Request request, DataOutputStream out) {
        while (!isTransferComplete) {
            Thread.onSpinWait();
        }
    }

    private void sendResponse(DataOutputStream out, int status, String statusMessage, String body, boolean includeContentType) throws IOException {
        StringBuilder response = new StringBuilder("HTTP/1.1 " + status + " " + statusMessage + "\r\n");

        if (includeContentType) {
            response.append("Content-Type: application/json\r\n");
            response.append("Content-Length: ").append(body.length()).append("\r\n");

            response.append("\r\n");
            response.append(body);
        }

        out.write(response.toString().getBytes());
    }
}
