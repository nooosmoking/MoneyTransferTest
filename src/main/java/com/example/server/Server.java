package com.example.server;

import com.example.controllers.BankController;
import com.example.exceptions.*;
import com.example.logger.Logger;
import com.example.models.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.security.sasl.AuthenticationException;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Scanner;

@Component
public class Server {
    private final BankController bankController;
    private final ExceptionHandler exceptionHandler;
    private final ResponseFactory responseFactory;
    private Scanner scanner;
    private ServerSocket server;
    private String url;
    private boolean isStopped = false;

    @Autowired
    public Server(BankController bankController, ExceptionHandler exceptionHandler, ResponseFactory responseFactory) {
        this.bankController = bankController;
        this.exceptionHandler = exceptionHandler;
        this.responseFactory = responseFactory;
    }

    public void run(String url, int port) {
        this.url = url;
        try (Scanner scanner = new Scanner(System.in); ServerSocket serverSocket = new ServerSocket(port)) {
            this.server = serverSocket;
            this.scanner = scanner;
            System.out.println("Starting server. For exiting write \"stop\"");
            startStdin();
            connectClients();
        } catch (IOException e) {
            System.err.println("Error while starting server.");
        } finally {
            close();
        }
    }

    private void startStdin() {
        Thread stdin = new Thread(() -> {
            while (true) {
                String answer = scanner.nextLine();
                if (answer.equalsIgnoreCase("stop")) {
                    close();
                }
            }
        });
        stdin.start();
    }

    private void connectClients() {
        while (!isStopped) {
            try {
                Socket clientSocket = server.accept();
                new ClientThread(clientSocket).start();
            } catch (IOException ignored) {
            }
        }
    }

    private void close() {
        if (server != null) {
            try {
                server.close();
            } catch (IOException ignored) {
                System.err.println("Error while closing logger");
            }
        }
        Logger logger;
        if ((logger = Logger.getInstance()) != null) {
            logger.close();
        }
        System.exit(0);
    }

    private class ClientThread extends Thread {
        private final Socket clientSocket;
        private DataOutputStream out;
        private BufferedReader in;
        private Map<String, String> requestHeaders;
        private String requestBody;
        private Response response;

        public ClientThread(Socket clientSocket) throws IOException {
            this.clientSocket = clientSocket;
        }

        public void run() {
            try (DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream()); BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                this.out = out;
                this.in = in;
                handleHttpRequest();
            } catch (IOException e) {
                System.err.println("Error while connecting client.");
            } catch (NullPointerException ex) {
                System.err.println("Error while generating response.");
            } finally {
                closeClientSocket();
            }
        }

        private void handleHttpRequest() throws IOException, NullPointerException {
            try {
                parseRequest();
                implementMethod();
            } catch (Exception ex) {
                response = exceptionHandler.handleException(ex);
            }
            sendResponse();
        }

        private void parseRequest() throws InvalidRequestException, IOException {
            RequestParser requestParser = new RequestParser(in, url);
            requestParser.parse();
            this.requestHeaders = requestParser.getRequestHeaders();
            this.requestBody = requestParser.getRequestBody();
        }

        private void implementMethod() throws JwtAuthenticationException, IOException, InvalidRequestException, ResourceNotFoundException, MethodNotAllowedException, AuthenticationException, UserAlreadyExistsException, NotEnoughMoneyException {
            String method = requestHeaders.get("method");
            switch (method.toUpperCase()) {
                case "GET":
                    handleGetRequest();
                    break;
                case "POST":
                    handlePostRequest();
                    break;
                default:
                    throw new MethodNotAllowedException("Method " + method + " not allowed.");
            }
        }

        private void handleGetRequest() throws ResourceNotFoundException, JwtAuthenticationException, IOException {
            String path = requestHeaders.get("path");
            if (!path.equals("money")) {
                throw new ResourceNotFoundException("Resource not found \"" + path + "\"");
            }
            response = bankController.getBalance(new Request(requestHeaders));
        }

        private void handlePostRequest() throws InvalidRequestException, ResourceNotFoundException, IOException, org.springframework.security.core.AuthenticationException, UserAlreadyExistsException, NotEnoughMoneyException, AuthenticationException {
            if (requestBody.isEmpty()) {
                throw new InvalidRequestException("Body is empty");
            }
            String path = requestHeaders.get("path");
            try {
                switch (path) {
                    case "money":
                        response = bankController.transferMoney(new TransferRequest(requestBody, requestHeaders));
                        break;
                    case "signup":
                        response = bankController.signup(new SignupRequest(requestBody));
                        break;
                    case "signin":
                        response = bankController.signin(new SigninRequest(requestBody));
                        break;
                    default:
                        throw new ResourceNotFoundException("Resource not found \"" + path + "\"");
                }
            } catch (JsonProcessingException ex) {
                throw new InvalidRequestException("Error while serialization body");
            }
        }

        private void sendResponse() throws IOException, NullPointerException {
            String responseStr = responseFactory.createResponseString(response);
            out.write(responseStr.getBytes());
        }

        private void closeClientSocket() {
            isStopped = true;
            if (clientSocket != null) {
                try {
                    clientSocket.close();
                } catch (IOException ex) {
                    System.err.println("Error while closing client socket");
                }
            }
        }
    }
}
