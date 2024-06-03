package com.example.server;

import com.example.controllers.BankController;
import com.example.exceptions.InvalidRequestException;
import com.example.exceptions.MethodNotAllowedException;
import com.example.exceptions.ResourceNotFoundException;
import com.example.models.SigninRequest;
import com.example.models.SignupRequest;
import com.example.models.TransferRequest;
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
            try (Socket client = server.accept(); DataOutputStream out = new DataOutputStream(client.getOutputStream()); BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()))) {
                new ClientThread(out, in).run();
            } catch (IOException e) {
                logger.error("Error while connecting client");
            }
        }
    }

    private class ClientThread {
        private final DataOutputStream out;
        private final BufferedReader in;
        private Map<String, String> headers;
        private String body;

        public ClientThread(DataOutputStream out, BufferedReader in) throws IOException {
            this.out = out;
            this.in = in;
            logger.info("New client connected");
        }

        public void run() {
            while (true) {
                handleHttpRequest();
            }
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
                System.err.println("Error while handling http request.");
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
                    throw new InvalidRequestException("Invalid http request start line.");
                }
            }
        }

        private void implementMethod() throws IOException, InvalidRequestException, ResourceNotFoundException, MethodNotAllowedException {
            String method = headers.get("method");
            String path = headers.get("path");
            if (method.equalsIgnoreCase("GET")) {
                if (path.equals("money")) {
                    bankController.getBalance("authToken", out);
                } else {
                    throw new ResourceNotFoundException("Resource not found \"" + path + "\"");
                }
            } else if (method.equalsIgnoreCase("POST")) {
                if (!body.isEmpty()) {
                    if (path.equals("money")) {
                        bankController.transferMoney(new TransferRequest(body), out);
                    } else if (path.equals("signup")) {
                        bankController.signup(new SignupRequest(body), out);
                    } else if (path.equals("signin")) {
                        bankController.signin(new SigninRequest(body), out);
                    } else {
                        throw new ResourceNotFoundException("Resource not found \"" + path + "\"");
                    }
                } else {
                    throw new InvalidRequestException("Body is empty");
                }
            } else {
                throw new MethodNotAllowedException("Method " + method + " not allowed");
            }
        }

        private String readBody() throws IOException {
            StringBuilder bodyBuilder = new StringBuilder();
            int length = Integer.parseInt(headers.get("Content-Length"));
            for (int i = 0; i < length; i++) {
                bodyBuilder.append((char) in.read());
            }
            return bodyBuilder.toString();
        }

        private void sendErrorResponse(int status, String statusMessage, String body
        ) throws IOException {
            StringBuilder response = new StringBuilder("HTTP/1.1 " + status + " " + statusMessage + "\r\n");


            response.append("Content-Type: application/json\r\n");
            response.append("Content-Length: ").append(body.length()).append("\r\n");

            response.append("\r\n");
            response.append(body).append("\r\n");

            out.write(response.toString().getBytes());

        }
    }

    public void close() {

    }
}
