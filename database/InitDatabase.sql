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
\c FinanceDB;
CREATE SCHEMA IF NOT EXISTS tables;
CREATE TABLE IF NOT EXISTS tables.users
(
    id         SERIAL PRIMARY KEY,
    firstname  VARCHAR(250) NOT NULL,
    surname    VARCHAR(250) NOT NULL,
    password   VARCHAR(250),
    email      VARCHAR(250) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

SELECT * FROM tables.users;