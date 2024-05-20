package com.example.repositories;

import java.util.List;

public interface MessageRepository extends CrudRepository<Message>{
    List<Message> findLastCountMessages(int count, String roomName);
}
