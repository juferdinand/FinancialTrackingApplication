CREATE SCHEMA IF NOT EXISTS tables;
CREATE TABLE IF NOT EXISTS tables.users
(
    user_id    VARCHAR(250) PRIMARY KEY NOT NULL,
    firstname  VARCHAR(250) NOT NULL,
    surname    VARCHAR(250) NOT NULL,
    email      VARCHAR(250) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO tables.users (user_id, firstname, surname, email) VALUES ('user_id', 'Max', 'Mustermann', 'MaxMustermann@example.de');