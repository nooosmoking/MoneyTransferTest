package com.example.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class TransferRequest {
    private int amount;
    private int senderId;
    private int receiverId;
}
