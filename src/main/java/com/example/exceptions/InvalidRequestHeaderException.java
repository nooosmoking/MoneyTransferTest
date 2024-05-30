package com.example.exceptions;

public class InvalidRequestHeaderException extends Exception {
    public InvalidRequestHeaderException(String message) {
        super(message);
    }
}