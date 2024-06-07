package com.example.models;

import lombok.Data;

@Data
public class User {
    private long id;
    private String login;
    private String password;
    private double balance;
    private String jwtToken;

    public User(String login, String password, double balance, String jwtToken) {
        this.login = login;
        this.password = password;
        this.balance = balance;
        this.jwtToken = jwtToken;
    }
}
