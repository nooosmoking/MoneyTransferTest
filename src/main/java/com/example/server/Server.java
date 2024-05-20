package com.example.server;

import com.example.controllers.BankController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;
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

        public ClientThread(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try {
                DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream());
                logger.info("New client connected");
                while (true) {
                    parseHeader(out, in);
                }
            } catch (IOException | NullPointerException ignored) {
            }
        }

        private void parseHeader(DataOutputStream out, BufferedReader in) throws IOException {
            String request = in.readLine();
            String[] parts = request.split(" ");
            try {
                String method = parts[0];
                String[] uri = parts[1].split("/");
                if(uri[0].equals(url)){
                    getMethod(out, in, method, uri[1]);
                }
            } catch (IndexOutOfBoundsException ex) {
                sendErrorMessage(out);
            }
        }

        private void getMethod(DataOutputStream out, BufferedReader in, String method, String path) throws IOException {
            String body = parseBody(in);
            if (method.equalsIgnoreCase("POST") && path.equals("money")) {
                bankController.processMoneyTransferRequest(in, out);
            } else if (method.equalsIgnoreCase("GET") && path.equals("money")) {
                bankController.processGetBalanceRequest(in, out);
            } else if (method.equalsIgnoreCase("POST") && path.equals("signup")) {
                bankController.processSignupRequest(in, out);
            } else if (method.equalsIgnoreCase("POST") && path.equals("signin")) {
                bankController.processSigninRequest(in, out);
            } else {
                sendErrorMessage(out);
            }
        }

        private String parseBody(BufferedReader in) throws IOException {
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
