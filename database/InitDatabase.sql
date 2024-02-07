DO
$$
    BEGIN
        IF NOT EXISTS (
            SELECT datname FROM pg_catalog.pg_database WHERE lower(datname) = lower('FinanceDB')
        ) THEN
            EXECUTE 'CREATE DATABASE FinanceDB';
        END IF;
    END
$$;
CREATE SCHEMA IF NOT EXISTS tables;
CREATE TABLE IF NOT EXISTS tables.users
(
    user_Id         VARCHAR(250) PRIMARY KEY unique ,
    firstname  VARCHAR(250) NOT NULL,
    surname    VARCHAR(250) NOT NULL,
    password   VARCHAR(250),
    token VARCHAR(8) unique ,
    token_valid_until TIMESTAMP,
    token_type VARCHAR(250),
    verified   BOOLEAN DEFAULT FALSE,
    email      VARCHAR(250) NOT NULL unique ,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);