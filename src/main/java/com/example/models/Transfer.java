package com.example.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
public class Transfer {
    private long id;
    private double amount;
    private User sender;
    private User receiver;
    private LocalDateTime localDateTime;

    public Transfer(double amount, User sender, User receiver) {
        this.amount = amount;
        this.sender = sender;
        this.receiver = receiver;
    }
}
