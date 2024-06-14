package com.example.repositories;

import com.example.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

@Repository("usersRepository")
public class UsersRepositoryImpl implements UsersRepository {
    private final DataSource dataSource;

    @Autowired
    public UsersRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    //
    @Override
    public List<User> findAll() {
        return null;
    }

    @Override
    public Optional<User> findById(long id) {
        NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        String query = "SELECT * FROM users WHERE id = :id";
        List<User> user = jdbcTemplate.query(query, new MapSqlParameterSource().addValue("id", id), new BeanPropertyRowMapper<>(User.class));
        return user.stream().findFirst();
    }

    @Override
    public void save(User entity) {
        NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        String login = entity.getLogin();
        if (findByLogin(login).isPresent()) {
            return;

        }
        String query = "INSERT INTO users (login, password, jwttoken) VALUES (:login, :password, :jwttoken);";
        jdbcTemplate.update(query, new MapSqlParameterSource()
                .addValue("login", login)
                .addValue("password", entity.getPassword())
                .addValue("jwttoken", entity.getJwtToken()));
    }

    @Override
    public void update(User entity) {
        NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        String query = "UPDATE users SET login=:login, password=:password, balance=:balance, jwttoken=:jwttoken WHERE id = :id;";
        jdbcTemplate.update(query, new MapSqlParameterSource()
                .addValue("login", entity.getLogin())
                .addValue("password", entity.getPassword())
                .addValue("balance", entity.getBalance())
                .addValue("jwttoken", entity.getJwtToken())
                .addValue("id", entity.getId()));
    }


    @Override
    public void delete(User entity) {
    }

    @Override
    public void updateBalance(User entity) {
        NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        String query = "UPDATE users SET balance = :balance WHERE id = :id;";
        jdbcTemplate.update(query, new MapSqlParameterSource().addValue("balance", entity.getBalance()).addValue("id", entity.getId()));
    }

    @Override
    public Optional<User> findByLogin(String login) {
        NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        String query = "SELECT * FROM users WHERE login = :login";
        List<User> user = jdbcTemplate.query(query, new MapSqlParameterSource().addValue("login", login), new BeanPropertyRowMapper<>(User.class));
        return user.stream().findFirst();
    }
}
