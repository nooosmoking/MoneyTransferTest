package com.example.controllers;

import com.example.exceptions.NoSuchUserException;
import com.example.exceptions.NotEnoughMoneyException;
import com.example.models.SigninRequest;
import com.example.models.SignupRequest;
import com.example.models.TransferRequest;
import com.example.services.AuthServiceImpl;
import com.example.services.BalanceServiceImpl;
import com.example.services.TransferServiceImpl;
import org.springframework.stereotype.Controller;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Controller
public class BankControllerImpl implements BankController {
    private final ExecutorService executorService;
    private final AuthServiceImpl authService;
    private final TransferServiceImpl transferService;
    private final BalanceServiceImpl balanceService;
    private volatile boolean isTransferComplete = true;

    public BankControllerImpl(AuthServiceImpl authService, TransferServiceImpl transferService, BalanceServiceImpl balanceService) {
        this.authService = authService;
        this.transferService = transferService;
        this.balanceService = balanceService;
        this.executorService = Executors.newSingleThreadExecutor();
    }

    @Override
    public void signup(SignupRequest request, DataOutputStream out) {

    }

    @Override
    public void signin(SigninRequest request, DataOutputStream out) {

    }

    @Override
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
    public void getBalance(String authToken, DataOutputStream out) {
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
