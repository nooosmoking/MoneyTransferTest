package com.example.server;

import com.example.controllers.BankController;
import com.example.exceptions.InvalidRequestHeaderException;
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
        try {
            this.server = new ServerSocket(Integer.parseInt(address[1]));
            this.url = address[0];
        } catch (IOException e) {
            logger.error("Error while starting server.");
            System.exit(-1);
        }
    }

    public void run() {
        System.out.println("Starting server. For exiting write \"stop\"");
        while (true) {
            try {
                Socket client = server.accept();
                new ClientThread(client).run();
            } catch (IOException e) {
                logger.error("Error while connecting client");
            }
        }
    }

    private class ClientThread {
        private final DataOutputStream out;
        private final BufferedReader in;

        public ClientThread(Socket clientSocket) throws IOException {
            this.out = new DataOutputStream(clientSocket.getOutputStream());
            this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            logger.info("New client connected");
        }

        public void run() {
            while (true) {
                handleHttpRequest();
            }
        }

        private void handleHttpRequest() {
            try {
                String body = readBody();
                String[] headers = parseHeader();

                if (body.isEmpty()) {
                    sendErrorMessage();
                } else {
                    getMethod(headers, body);
                }
            } catch (IOException ex) {
                System.err.println("Error while handling http request.");
                ex.printStackTrace();
            } catch (InvalidRequestHeaderException ex) {
                System.err.println(ex.getMessage());
            } catch (NullPointerException ignored) {
            }
        }

        private String[] parseHeader() throws IOException, InvalidRequestHeaderException {
            String request = in.readLine();
            String[] parts = request.split(" ");
            try {
                String method = parts[0];
                String[] uri = parts[1].split("/");
                if (uri[0].equals(url) || uri[0].isEmpty()) {
                    return new String[]{method, uri[1]};
                } else {
                    throw new InvalidRequestHeaderException("Unknown request URL.");
                }
            } catch (IndexOutOfBoundsException | NullPointerException ex) {
                sendErrorMessage();
                throw new InvalidRequestHeaderException("Invalid http request headers.");
            }
        }

        private void getMethod(String[] headers, String body) throws IOException {
            String method = headers[0];
            String path = headers[1];
            if (method.equalsIgnoreCase("POST") && path.equals("money")) {
                bankController.transferMoney(new TransferRequest(body), out);
            } else if (method.equalsIgnoreCase("GET") && path.equals("money")) {
                bankController.getBalance("authToken", out);
            } else if (method.equalsIgnoreCase("POST") && path.equals("signup")) {
                bankController.signup(new SignupRequest(body), out);
            } else if (method.equalsIgnoreCase("POST") && path.equals("signin")) {
                bankController.signin(new SigninRequest(body), out);
            } else {
                sendErrorMessage();
            }
        }

        private String readBody() throws IOException {
            StringBuilder bodyBuilder = new StringBuilder();
            int b;
            while ((b = in.read()) != -1) {
                bodyBuilder.append((char) b);
            }
            return bodyBuilder.toString();
        }

        private void sendErrorMessage() throws IOException {
            out.writeUTF("400 Bad Request");
        }
    }

    public void close() {

    }
}
