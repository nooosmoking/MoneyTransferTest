package com.example.server;

import com.example.controllers.BankController;
import com.example.exceptions.InvalidRequestException;
import com.example.exceptions.MethodNotAllowedException;
import com.example.exceptions.ResourceNotFoundException;
import com.example.models.SigninRequest;
import com.example.models.SignupRequest;
import com.example.models.TransferRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

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
    private ServerSocket server;
    private final Scanner scanner = new Scanner(System.in);
    private final BankController bankController;
    private String url;

    public Server(BankController bankController) {
        this.bankController = bankController;
    }

    public void setAddress(String[] address) {
        int port = Integer.parseInt(address[1]);
        try {
            this.server = new ServerSocket(port);
            this.url = address[0];
        } catch (IOException e) {
            logger.error("Error while starting server.");
        }
    }

    public void run() {
        System.out.println("Starting server. For exiting write \"stop\"");
        while (true) {
            System.out.println("aaaaa");
            try {
                Socket clientSocket = server.accept();
                new ClientThread(clientSocket).start();
                System.out.println("bbbb");
            } catch (IOException e) {
                logger.error("Error while connecting client");
            }
        }
    }

    private class ClientThread extends Thread {
        private final DataOutputStream out;
        private final BufferedReader in;
        private Map<String, String> headers;
        private String body;

        public ClientThread(Socket clientSocket) throws IOException {
            System.out.println("New client");
            this.out = new DataOutputStream(clientSocket.getOutputStream());
            this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            logger.info("New client connected");
        }

        public void run() {
            System.out.println("Client started");
            handleHttpRequest();
        }

        private void handleHttpRequest() {
            try {
                try {
                    parseStartLine();
                    parseHeader();
                    body = readBody();
                    implementMethod();
                } catch (InvalidRequestException ex) {
                    System.err.println(ex.getMessage());
                    sendErrorResponse(400, "Bad Request", "{\"message\": \"" + ex.getMessage() + "\"}");
                } catch (ResourceNotFoundException ex) {
                    System.err.println(ex.getMessage());
                    sendErrorResponse(404, "Not Found", "{\"message\": \"" + ex.getMessage() + "\"}");
                } catch (MethodNotAllowedException ex) {
                    System.err.println(ex.getMessage());
                    sendErrorResponse(405, "Method Not Allowed ", "{\"message\": \"" + ex.getMessage() + "\"}");
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                System.err.println("Error while handling http request.");
                System.exit(-1);
            } catch (NullPointerException ignored) {
            }

        }

        private void parseStartLine() throws IOException, InvalidRequestException {
            headers = new HashMap<>();
            String request = in.readLine();
            String[] parts = request.split(" ");
            try {
                headers.put("method", parts[0]);
                String[] uri = parts[1].split("/");
                if (uri[0].equals(url) || uri[0].isEmpty()) {
                    headers.put("path", uri[1]);
                } else {
                    throw new InvalidRequestException("Unknown request URL.");
                }
            } catch (IndexOutOfBoundsException | NullPointerException ex) {
                throw new InvalidRequestException("Invalid http request start line.");
            }
        }

        private void parseHeader() throws IOException, InvalidRequestException {
            String request;
            while (!(request = in.readLine()).isEmpty()) {

                String[] parts = request.split(": ");
                try {
                    headers.put(parts[0], parts[1]);
                    String[] uri = parts[1].split("/");
                } catch (IndexOutOfBoundsException | NullPointerException ex) {
                    throw new InvalidRequestException("Invalid http header line.");
                }
            }
        }

        private void implementMethod() throws IOException, InvalidRequestException, ResourceNotFoundException, MethodNotAllowedException {
            String method = headers.get("method");
            String path = headers.get("path");

            switch (method.toUpperCase()) {
                case "GET":
                    handleGetRequest(path);
                    break;
                case "POST":
                    handlePostRequest(path, body);
                    break;
                default:
                    throw new MethodNotAllowedException("Method " + method + " not allowed.");
            }
        }

        private void handleGetRequest(String path) throws ResourceNotFoundException {
            if (!path.equals("money")) {
                throw new ResourceNotFoundException("Resource not found \"" + path + "\"");
            }
            bankController.getBalance("authToken", out);
        }

        private void handlePostRequest(String path, String body) throws InvalidRequestException, ResourceNotFoundException {
            if (body.isEmpty()) {
                throw new InvalidRequestException("Body is empty");
            }
            try {
                switch (path) {
                    case "money":
                        bankController.transferMoney(new TransferRequest(body), out);
                        break;
                    case "signup":
                        bankController.signup(new SignupRequest(body), out);
                        break;
                    case "signin":
                        bankController.signin(new SigninRequest(body), out);
                        break;
                    default:
                        throw new ResourceNotFoundException("Resource not found \"" + path + "\"");
                }
            } catch (JsonProcessingException ex) {
                throw new InvalidRequestException("Error while serialization body");
            }

        }

        private String readBody() throws IOException {
            String lengthString;
            if ((lengthString = headers.get("Content-Length")) == null) {
                return null;
            }
            StringBuilder bodyBuilder = new StringBuilder();
            int length = Integer.parseInt(headers.get("Content-Length"));
            for (int i = 0; i < length; i++) {
                bodyBuilder.append((char) in.read());
            }
            return bodyBuilder.toString();
        }

        private void sendErrorResponse(int status, String statusMessage, String body) throws IOException {
            String response = "HTTP/1.1 " + status + " " + statusMessage + "\r\nContent-Type: application/json\r\nContent-Length: "
                    + body.length() + "\r\n\r\n" + body;

            out.write(response.getBytes());
        }
    }

    public void close() {

    }
}
