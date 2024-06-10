package com.example.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
public class TransferRequest extends Request {
    private double amount;
    @JsonProperty("to")
    private long receiverId;

    public TransferRequest(String json, Map<String, String> headers) throws JsonProcessingException {
        super(headers);
        ObjectMapper mapper = new ObjectMapper();
        TransferRequest transferRequest = mapper.readValue(json, TransferRequest.class);
        this.amount = transferRequest.getAmount();
        this.setLogin(transferRequest.getLogin());
        this.receiverId = transferRequest.getReceiverId();
    }
}
