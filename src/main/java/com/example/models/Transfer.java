package com.example.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
public class Transfer {
    private long id;
    private double amount;
    private long senderId;
    private long receiverId;
    private LocalDateTime localDateTime;

    public Transfer(double amount, long senderId, long receiverId) {
        this.amount = amount;
        this.senderId = senderId;
        this.receiverId = receiverId;
    }
}
