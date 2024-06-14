package com.example.server;

import com.example.exceptions.InvalidRequestException;
import lombok.Data;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Data
public class RequestParser {
    private final BufferedReader in;
    private final String url;
    private Map<String, String> requestHeaders;
    private String requestBody;

    public RequestParser(BufferedReader in, String url) {
        this.in = in;
        this.url = url;
    }

    public void parse() throws InvalidRequestException, IOException {
        parseStartLine();
        parseHeader();
        readBody();
    }

    private void parseStartLine() throws IOException, InvalidRequestException {
        requestHeaders = new HashMap<>();
        String request = in.readLine();
        String[] parts = request.split(" ");
        try {
            requestHeaders.put("method", parts[0]);
            String[] uri = parts[1].split("/");
            if (uri[0].equals(url) || uri[0].isEmpty()) {
                requestHeaders.put("path", uri[1]);
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
                requestHeaders.put(parts[0], parts[1]);
                String[] uri = parts[1].split("/");
            } catch (IndexOutOfBoundsException | NullPointerException ex) {
                throw new InvalidRequestException("Invalid http header line.");
            }
        }
    }

    private void readBody() throws IOException {
        if (requestHeaders.get("Content-Length") == null) {
            return;
        }
        StringBuilder bodyBuilder = new StringBuilder();
        int length = Integer.parseInt(requestHeaders.get("Content-Length"));
        for (int i = 0; i < length; i++) {
            bodyBuilder.append((char) in.read());
        }
        requestBody = bodyBuilder.toString();
    }
}
