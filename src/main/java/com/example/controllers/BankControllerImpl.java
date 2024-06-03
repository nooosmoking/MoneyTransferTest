package com.example.controllers;

import com.example.exceptions.NotEnoughMoneyException;
import com.example.models.SigninRequest;
import com.example.models.SignupRequest;
import com.example.models.TransferRequest;
import com.example.services.AuthService;
import com.example.services.BalanceService;
import com.example.services.TransferService;
import org.springframework.stereotype.Controller;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Controller
public class BankControllerImpl implements BankController {
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
    public void signup(SignupRequest request, DataOutputStream out) {

    }

    @Override
    public void signin(SigninRequest request, DataOutputStream out) {

    }

    @Override
    public void transferMoney(TransferRequest request, DataOutputStream out) {
        executorService.execute(() -> {
                    try {
                        try {
                            transferService.transfer(request);
                            sendResponse(out, 200, "OK", "", false);
                        } catch (NotEnoughMoneyException ex) {
                            sendResponse(out, 400, "Bad Request", "{\"message\": " + ex.getMessage() + "}", true);
                        }
                    } catch (IOException ex) {
                        System.err.println("Error while sending http response.");
                    }
                }
        );
    }

    @Override
    public void getBalance(String authToken, DataOutputStream out) {

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
