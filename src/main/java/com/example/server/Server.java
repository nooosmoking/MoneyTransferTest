package com.example.server;

import com.example.controllers.BankController;
import com.example.exceptions.*;
import com.example.models.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.security.sasl.AuthenticationException;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

@Component
public class Server {
    private static final Logger logger = LoggerFactory.getLogger(Server.class);
    private final Scanner scanner = new Scanner(System.in);
    private ServerSocket server;
    private final BankController bankController;
    private String url;

    public Server(BankController bankController) {
        this.bankController = bankController;
    }

    public void run(String[] address) {
        int port = Integer.parseInt(address[1]);
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            this.server = serverSocket;
            this.url = address[0];
            System.out.println("Starting server. For exiting write \"stop\"");

            connectClients();
        } catch (IOException e) {
            logger.error("Error while starting server.");
        }
    }

    private void connectClients() {
        while (true) {
            try {
                Socket clientSocket = server.accept();
                new ClientThread(clientSocket).start();
            } catch (IOException e) {
                logger.error("Error while connecting client");
            }
        }
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
            logger.info("New client connected");
        }

        public void run() {
            try (DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream()); BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                this.out = out;
                this.in = in;
                handleHttpRequest();
            } catch (IOException e) {
                System.err.println("Error while connecting client.");
            } finally {
                closeClientSocket();
            }
        }

        private void handleHttpRequest() throws IOException {
            try {
                parseRequest();
                implementMethod();
            } catch (InvalidRequestException | NotEnoughMoneyException | IllegalArgumentException ex) {
                response = new Response(400, "Bad Request", "{\"message\": \"" + ex.getMessage() + "\"}");
            } catch (ResourceNotFoundException | NoSuchUserException ex) {
                response = new Response(404, "Not Found", "{\"message\": \"" + ex.getMessage() + "\"}");
            } catch (MethodNotAllowedException ex) {
                response = new Response(405, "Method Not Allowed", "{\"message\": \"" + ex.getMessage() + "\"}");
            } catch (JwtAuthenticationException | AuthenticationException ex) {
                response = new Response(403, "Forbidden ", "{\"message\": \"" + ex.getMessage() + "\"}");
            } catch (UserAlreadyExistsException ex) {
                response = new Response(409, "Conflict", "{\"message\": \"" + ex.getMessage() + "\"}");
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

        private void sendResponse() throws IOException {
            String responseStr = "HTTP/1.1 " + response.getStatus() + " " + response.getStatusMessage() + "\r\nContent-Type: application/json\r\nContent-Length: " + response.getBody().length() + "\r\n\r\n" + response.getBody();

            out.write(responseStr.getBytes());
        }

        private void closeClientSocket(){
            try {
                clientSocket.close();
            } catch (IOException ignored) {
            }
        }
    }

    public void close() {

    }
}
