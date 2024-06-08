package com.example.controllers;

import com.example.controllers.aspects.AuthAspect;
import com.example.controllers.aspects.AuthRequired;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;

import java.io.DataOutputStream;
import java.io.IOException;

@Controller
public class BankControllerImpl implements BankController {
    private final AuthServiceImpl authService;
    private final TransferServiceImpl transferService;
    private final BalanceServiceImpl balanceService;
    private boolean isTransferComplete = true;
    private final AuthAspect authAspect;

    @Autowired
    public BankControllerImpl(AuthServiceImpl authService, TransferServiceImpl transferService, BalanceServiceImpl balanceService, AuthAspect authAspect) {
        this.authService = authService;
        this.transferService = transferService;
        this.balanceService = balanceService;
        this.authAspect = authAspect;
    }

    @Override
    public void signup(SignupRequest request, DataOutputStream out) throws UserAlreadyExistsException, IOException {
        String token = authService.signUp(request);
        sendResponse(out, 200, "OK", "{\"token\":\"" + token + "\"}", true);

    }

    @Override
    public void signin(SigninRequest request, DataOutputStream out) throws AuthenticationException, NoSuchUserException, IOException {
        String token = authService.signIn(request);
        sendResponse(out, 200, "OK", "{\"token\":\"" + token + "\"}", true);
    }

    @Override
    @AuthRequired
    public void transferMoney(TransferRequest request, DataOutputStream out) throws NotEnoughMoneyException, NoSuchUserException, IllegalArgumentException, IOException {
        isTransferComplete = false;
        try {
            transferService.transfer(request);
            sendResponse(out, 200, "OK", "", false);
        } finally {
            isTransferComplete = true;
        }
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
