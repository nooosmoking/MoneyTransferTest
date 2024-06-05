package com.example.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TransferRequest {
    private double amount;
    @JsonProperty("senderid")
    private long senderId;
    @JsonProperty("receiverid")
    private long receiverId;
    @JsonIgnore
    private String jwtToken;

    public TransferRequest(String json, String jwtToken) throws JsonProcessingException {
        ObjectMapper mapper =
                new ObjectMapper();
        TransferRequest transferRequest = mapper.readValue(json, TransferRequest.class);
        this.amount = transferRequest.getAmount();
        this.senderId = transferRequest.getSenderId();
        this.receiverId = transferRequest.getReceiverId();
        this.jwtToken = jwtToken;
    }
}
