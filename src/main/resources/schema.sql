DROP TABLE IF EXISTS users, transfers CASCADE;

SET TIME ZONE 'GMT';

CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY NOT NULL,
    login VARCHAR(30) NOT NULL,
    password VARCHAR(1000) NOT NULL,
    balance INTEGER NOT NULL CHECK (amount > 0)
);

CREATE TABLE IF NOT EXISTS transfers (
    id SERIAL PRIMARY KEY NOT NULL,
    amount INTEGER NOT NULL CHECK (amount > 0),
    sender INTEGER REFERENCES users(id) NOT NULL,
    receiver INTEGER REFERENCES users(id) NOT NULL,
    date_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP PRIMARY KEY NOT NULL
    );

INSERT INTO users (login, password, balance) VALUES ('Ann', 'aa', 100), ('Ivan', 'ii', 100), ('Nikolay', 'nn', 100);

INSERT INTO transfers (amount, sender, receiver) VALUES (100, 2, 1);

