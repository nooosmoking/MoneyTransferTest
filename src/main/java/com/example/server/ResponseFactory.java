package com.example.server;

import com.example.models.Response;
import org.springframework.stereotype.Component;

@Component
public class ResponseFactory {
    public String createResponseString(Response response) throws NullPointerException {
        return "HTTP/1.1 " + response.getStatus() + " " + response.getStatusMessage() + "\r\nContent-Type: application/json\r\nContent-Length: " + response.getBody().length() + "\r\n\r\n" + response.getBody();
    }
}
