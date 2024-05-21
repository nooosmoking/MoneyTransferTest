package com.example.exceptions;

public class
NotEnoughMoneyException extends RuntimeException {
    public NotEnoughMoneyException(String msg){
        super(msg);
    }
}
