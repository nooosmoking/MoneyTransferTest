package com.example.services;

import com.example.models.Request;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BalanceServiceImpl implements BalanceService{
    @Override
    public int getBalance(Request request) {
        return 0;
    }
}
