package com.example.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

//@Repository("usersRepository")
//public class UsersRepositoryImpl implements UsersRepository {
//    private final DataSource dataSource;
//
//    @Autowired
//    public UsersRepositoryImpl(DataSource dataSource) {
//        this.dataSource = dataSource;
//    }
//
//    @Override
//    public List<User> findAll() {
//        NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
//        String query = "SELECT * FROM users";
//        RowMapper<User> userRowMapper = (r, i) -> {
//            User rowUser = new User();
//            rowUser.setLogin(r.getString("login"));
//            rowUser.setPassword(r.getString("password"));
//            rowUser.setIn(null);
//            rowUser.setOut(null);
//            rowUser.setActive(false);
//            return rowUser;
//        };
//        return jdbcTemplate.query(query, userRowMapper);
//    }
//
//    @Override
//    public boolean save(User entity) {
//        NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
//        if(findByLogin(entity.getLogin()).isPresent()){
//            return false;
//        }
//        String query = "INSERT INTO users (login, password) VALUES (:login, :password);";
//        jdbcTemplate.update(query, new MapSqlParameterSource()
//                .addValue("login", entity.getLogin())
//                .addValue("password", entity.getPassword()));
//        return true;
//    }
//
//    @Override
//    public void update(User entity) {
//        NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
//        String query = "UPDATE users SET login = :login, password = :password WHERE id = :id;";
//        jdbcTemplate.update(query, new BeanPropertySqlParameterSource(entity));
//    }
//
//
//    @Override
//    public void delete(String name) {
//        NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
//        String query = "DELETE FROM users WHERE login = :login;";
//        jdbcTemplate.update(query, new MapSqlParameterSource().addValue("login", name));
//    }
//
//    @Override
//    public Optional<User> findByLogin(String login) {
//        NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
//        String query = "SELECT * FROM users WHERE login = :login";
//        List<User> user = jdbcTemplate.query(query, new MapSqlParameterSource().addValue("login", login), new BeanPropertyRowMapper<>(User.class));
//        return user.stream().findFirst();
//    }
//}
