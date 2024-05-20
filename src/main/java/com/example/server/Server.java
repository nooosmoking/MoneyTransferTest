package com.example.server;

import com.example.controllers.BankController;
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
                new ClientThread(client).start();
            } catch (IOException e) {
                logger.error("Error while connecting client");
            }
        }
    }

    private class ClientThread extends Thread {
        private final Socket clientSocket;
        private final DataOutputStream out;
        private final BufferedReader in;

        public ClientThread(Socket clientSocket) throws IOException {
            this.clientSocket = clientSocket;
            this.out = new DataOutputStream(clientSocket.getOutputStream());
            this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            logger.info("New client connected");
        }
        @Override
        public void run() {
            try {
                while (true) {
                    handleHttpRequest();
                }
            } catch (IOException | NullPointerException ignored) {
            }
        }

        private void handleHttpRequest() throws IOException {
            String[] headers = parseHeader();
            if (headers != null) {
                String body = readBody(in);
                getMethod(headers, body);
            }
        }

        private String[] parseHeader() throws IOException {
            String request = in.readLine();
            String[] parts = request.split(" ");
            try {
                String method = parts[0];
                String[] uri = parts[1].split("/");
                if (uri[0].equals(url)) {
                    return new String[]{method, uri[1]};
                }
            } catch (IndexOutOfBoundsException ex) {
                sendErrorMessage(out);
            }
            return null;
        }

        private void getMethod(String[] headers, String body) throws IOException {
            String method = headers[0];
            String path = headers[1];
            if (method.equalsIgnoreCase("POST") && path.equals("money")) {
                bankController.transferMoney(body, out);
            } else if (method.equalsIgnoreCase("GET") && path.equals("money")) {
                bankController.getBalance(body, out);
            } else if (method.equalsIgnoreCase("POST") && path.equals("signup")) {
                bankController.signup(body, out);
            } else if (method.equalsIgnoreCase("POST") && path.equals("signin")) {
                bankController.signin(body, out);
            } else {
                sendErrorMessage(out);
            }
        }

        private String readBody(BufferedReader in) throws IOException {
            StringBuilder bodyBuilder = new StringBuilder();
            int b;
            while ((b = in.read()) != -1) {
                bodyBuilder.append((char) b);
            }
            return bodyBuilder.toString();
        }

        private void sendErrorMessage(DataOutputStream out) throws IOException {
            out.writeUTF("HTTP/1.1 404 Not Found");
        }
    }


    public void close() {

    }
}
