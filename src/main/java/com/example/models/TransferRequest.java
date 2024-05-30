package com.example.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class TransferRequest {
    private int amount;
    private long senderId;
    private long receiverId;

    public TransferRequest(String json) throws JsonProcessingException {
        ObjectMapper mapper =
                new ObjectMapper();
        TransferRequest transferRequest = mapper.readValue(json, TransferRequest.class);
        this.amount = transferRequest.getAmount();
        this.senderId = transferRequest.getSenderId();
        this.receiverId = transferRequest.getReceiverId();
    }
}
