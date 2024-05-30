package com.example.repositories;

import java.util.List;
import java.util.Optional;

public interface CrudRepository<T> {

    List<T> findAll();

    Optional<T> findById(long id);

    void save(T entity);

    void update(T entity);

    void delete(int id);
}
