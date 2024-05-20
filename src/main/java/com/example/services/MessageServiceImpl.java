package com.example.services;

import com.example.repositories.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("messageService")
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;

    @Autowired
    public MessageServiceImpl(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    @Transactional
    public List<Message> findLastCountMessages(int count, String roomName) {
        return messageRepository.findLastCountMessages(count, roomName);
    }

    @Override
    @Transactional
    public void save(Message msg) {
        messageRepository.save(msg);
    }
}
