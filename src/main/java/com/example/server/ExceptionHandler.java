package com.example.server;

import com.example.exceptions.*;
import com.example.models.Response;
import org.springframework.stereotype.Component;

import javax.security.sasl.AuthenticationException;

@Component
public class ExceptionHandler {
    public Response handleException(Exception ex) {
        int status;
        String message;

        if (ex instanceof InvalidRequestException || ex instanceof IllegalArgumentException) {
            status = 400;
            message = "Bad Request";
        } else if (ex instanceof ResourceNotFoundException || ex instanceof NoSuchUserException) {
            status = 404;
            message = "Not Found";
        } else if (ex instanceof MethodNotAllowedException) {
            status = 405;
            message = "Method Not Allowed";
        } else if (ex instanceof JwtAuthenticationException || ex instanceof AuthenticationException) {
            status = 403;
            message = "Forbidden";
        } else if (ex instanceof UserAlreadyExistsException) {
            status = 409;
            message = "Conflict";
        } else {
            status = 500;
            message = "Internal Server Error";
        }

        String body = "{\"message\": \"" + ex.getMessage() + "\"}";
        return new Response(status, message, body);
    }
}
