package com.example.services;

import com.example.exceptions.NoSuchUserException;
import com.example.exceptions.NotEnoughMoneyException;
import com.example.models.Transfer;
import com.example.models.TransferRequest;
import com.example.models.User;
import com.example.repositories.TransferRepository;
import com.example.repositories.UsersRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class TransferServiceImpl implements TransferService{
    private UsersRepository usersRepository;
    private TransferRepository transferRepository;

    public TransferServiceImpl(UsersRepository usersRepository, TransferRepository transferRepository) {
        this.usersRepository = usersRepository;
        this.transferRepository = transferRepository;
    }

    public synchronized void transfer(TransferRequest request) throws NoSuchUserException, NotEnoughMoneyException, IllegalArgumentException {
        Optional<User> senderOptional = usersRepository.findByLogin(request.getLogin());
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
        if (senderOptional.isEmpty()){
            throw new NoSuchUserException("Error while authenticate.");
        } else if (receiverOptional.isEmpty()){
            throw new NoSuchUserException("There is no user with id "+request.getReceiverId());
        }
        User sender = senderOptional.get();
        User receiver = receiverOptional.get();
        if (receiver.getId() == sender.getId()){
            throw new IllegalArgumentException("Forbidden to send money to yourself.");
        }
        if (sender.getBalance() < request.getAmount()){
            throw new NotEnoughMoneyException("User with id " + sender.getId() +" have not enough money to make transaction.");
        }
    }
}
