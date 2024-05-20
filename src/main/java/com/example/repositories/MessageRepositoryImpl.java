package com.example.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Repository("messageRepository")
public class MessageRepositoryImpl implements MessageRepository{
    private final DataSource dataSource;

    @Autowired
    public MessageRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Message> findAll() {
        NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        String query = "SELECT m.text, m.date_time, u.login FROM messages m " +
                "LEFT JOIN users u ON m.sender = u.login";
        RowMapper<Message> messageRowMapper = (r, i) -> {
            Message rowMessage = new Message();
            rowMessage.setSender(new User( r.getString("login"), null, null, null, false));
            rowMessage.setText(r.getString("text"));
            rowMessage.setTime(convertToLocalDateTime(r.getTimestamp("date_time")));
            return rowMessage;
        };
        return jdbcTemplate.query(query, messageRowMapper);
    }

    private LocalDateTime convertToLocalDateTime(Timestamp timestamp) {
        ZoneId gmtZone = ZoneId.of("GMT");
        ZoneId localZone = ZoneId.systemDefault();

        return timestamp.toLocalDateTime().atZone(gmtZone).withZoneSameInstant(localZone).toLocalDateTime();
    }


    @Override
    public boolean save(Message entity) {
        NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        String query = "INSERT INTO messages (text, sender, room) VALUES (:text, :sender, :room);";
        jdbcTemplate.update(query, new MapSqlParameterSource()
                .addValue("text", entity.getText())
                .addValue("sender", entity.getSender().getLogin())
                .addValue("room", entity.getRoom().getName()));
        return true;
    }

    @Override
    public void update(Message entity) {

    }

    @Override
    public void delete(String name) {

    }

    @Override
    public List<Message> findLastCountMessages(int count, String roomName) {
        NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        String query = "SELECT  m.text, m.date_time, m.room, u.login FROM messages m " +
                "LEFT JOIN users u ON m.sender = u.login "+
                "WHERE room = :room "+
                "ORDER BY m.date_time "+
                "LIMIT :count";
        RowMapper<Message> messageRowMapper = (r, i) -> {
            Message rowMessage = new Message();
            rowMessage.setSender(new User( r.getString("login"), null, null, null, false));
            rowMessage.setText(r.getString("text"));
            rowMessage.setTime(convertToLocalDateTime(r.getTimestamp("date_time")));
            rowMessage.setRoom(new Chatroom(r.getString("room"), null));
            return rowMessage;
        };
        return jdbcTemplate.query(query, new MapSqlParameterSource()
                .addValue("count", count)
                .addValue("room", roomName)
                ,messageRowMapper);
    }
}
