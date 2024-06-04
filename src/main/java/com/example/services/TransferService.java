package com.example.services;

import com.example.exceptions.NoSuchUserException;
import com.example.exceptions.NotEnoughMoneyException;
import com.example.models.TransferRequest;

public interface TransferService {
    void transfer(TransferRequest request) throws NoSuchUserException, NotEnoughMoneyException, IllegalArgumentException;
}
