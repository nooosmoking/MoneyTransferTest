package com.example.models;

import lombok.Data;

@Data
public class User {
    private long id;
    private String login;
    private String password;
    private double balance;

    public User(String login, String password, double balance) {
        this.login = login;
        this.password = password;
        this.balance = balance;
    }
}
