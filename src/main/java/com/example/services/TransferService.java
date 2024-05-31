package com.example.services;

import com.example.exceptions.NoSuchUserException;
import com.example.exceptions.NotEnoughMoneyException;
import com.example.models.Transfer;
import com.example.models.TransferRequest;
import com.example.models.User;
import com.example.repositories.TransferRepository;
import com.example.repositories.UsersRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TransferService {
    private UsersRepository usersRepository;
    private TransferRepository transferRepository;

    public TransferService(UsersRepository usersRepository, TransferRepository transferRepository) {
        this.usersRepository = usersRepository;
        this.transferRepository = transferRepository;
    }

    public void transfer(TransferRequest request) throws NoSuchUserException, NotEnoughMoneyException {
        Optional<User> senderOptional = usersRepository.findById(request.getSenderId());
        Optional<User> receiverOptional = usersRepository.findById(request.getReceiverId());

        validateTransfer(senderOptional, receiverOptional, request);

        User sender = senderOptional.get();
        sender.setBalance(sender.getBalance()-request.getAmount());
        User receiver = receiverOptional.get();
        receiver.setBalance(receiver.getBalance()+request.getAmount());

        Transfer transfer = new Transfer(request.getAmount(), sender, receiver);
        transferRepository.save(transfer);

        usersRepository.updateBalance(sender);
        usersRepository.updateBalance(receiver);
    }

    private void validateTransfer(Optional<User> senderOptional, Optional<User> receiverOptional, TransferRequest request) throws NotEnoughMoneyException, NoSuchUserException {
        if (!senderOptional.isPresent()){
            throw new NoSuchUserException("There is no user with id "+request.getSenderId());
        } else if (!receiverOptional.isPresent()){
            throw new NoSuchUserException("There is no user with id "+request.getReceiverId());
        }
        User sender = senderOptional.get();
        if (sender.getBalance() < request.getAmount()){
            throw new NotEnoughMoneyException("User with id " + sender.getId() +" have not enough money to make transaction.");
        }
    }
}
