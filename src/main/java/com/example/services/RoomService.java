package com.example.services;

import java.util.List;
import java.util.Optional;

public interface RoomService {
    List<Chatroom> findAllRooms();
    Optional<Chatroom> findRoomInList(List<Chatroom> rooms, String name);
    boolean createRoom(String name, User user, List<Chatroom> rooms);
    Optional<Chatroom> chooseRoom(int roomIndex, User user, List<Chatroom> rooms);
}
