package com.example.services;

import com.example.exceptions.NoSuchUserException;
import com.example.logger.Logger;
import com.example.models.Request;
import com.example.models.User;
import com.example.repositories.UsersRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class BalanceServiceImpl implements BalanceService {
    private final UsersRepository usersRepository;

    public BalanceServiceImpl(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Override
    public double getBalance(Request request) throws NoSuchUserException {
        String login = request.getLogin();
        Optional<User> optionalUser = usersRepository.findByLogin(login);
        if (optionalUser.isEmpty()) {
            throw new NoSuchUserException("User with login " + login + " not exists.");
        }
        double balance = optionalUser.get().getBalance();
        Logger.getInstance().logUserBalance(login, balance);
        return balance;
    }
}
