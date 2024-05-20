package com.example.services;

import java.util.List;

public interface MessageService {
    List<Message> findLastCountMessages(int count, String roomName);

    void save(Message msg);
}
