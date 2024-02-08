CREATE SCHEMA IF NOT EXISTS tables;
CREATE TABLE IF NOT EXISTS tables.users
(
    user_Id           VARCHAR(250) PRIMARY KEY unique,
    firstname         VARCHAR(250) NOT NULL,
    surname           VARCHAR(250) NOT NULL,
    password          VARCHAR(250),
    token             VARCHAR(8),
    token_valid_until TIMESTAMP,
    token_type        VARCHAR(250),
    verified          BOOLEAN   DEFAULT FALSE,
    email             VARCHAR(250) NOT NULL unique,
    version           BIGINT    DEFAULT 0,
    avatar_url        VARCHAR(250),
    created_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO tables.users
(user_id, firstname, surname, email, verified, token, token_valid_until, token_type,password)
VALUES ('user_id', 'Max', 'Mustermann', 'Max.Mustermann@example.de', false, 'token', NOW() + INTERVAL '1 day', 'EMAIL_VERIFICATION',
     '$argon2i$v=19$m=65536,t=2,p=1$by2LL/w2ShxPWgNLcxPiMQ$eu+b6YAWyzbrQx5io8hu6t3xGpsGqkn6ZIxNCljEjQU');

INSERT INTO tables.users
(user_id, firstname, surname, email, verified, token, token_valid_until, token_type,password)
VALUES ('user_id_email_verification', 'Jens', 'Mustermann', 'Jens.Mustermann@example.de', false, 'token4', NOW() + INTERVAL '1 day', 'EMAIL_VERIFICATION',
        '$argon2i$v=19$m=65536,t=2,p=1$by2LL/w2ShxPWgNLcxPiMQ$eu+b6YAWyzbrQx5io8hu6t3xGpsGqkn6ZIxNCljEjQU');

INSERT INTO tables.users
(user_id, firstname, surname, email, verified, token, token_valid_until, token_type,password)
VALUES ('user_id_password_reset', 'Noah', 'Mustermann', 'Noah.Mustermann@example.de', true, 'token3', NOW() + INTERVAL '1 day', 'PASSWORD_RESET',
        '$argon2i$v=19$m=65536,t=2,p=1$by2LL/w2ShxPWgNLcxPiMQ$eu+b6YAWyzbrQx5io8hu6t3xGpsGqkn6ZIxNCljEjQU');

INSERT INTO tables.users
(user_id, firstname, surname, email, verified, token, token_valid_until, token_type,password)
VALUES ('user_id_password_reset_2', 'Nils', 'Mustermann', 'Nils.Mustermann@example.de', true, 'token5', NOW() + INTERVAL '1 day', 'PASSWORD_RESET',
        '$argon2i$v=19$m=65536,t=2,p=1$by2LL/w2ShxPWgNLcxPiMQ$eu+b6YAWyzbrQx5io8hu6t3xGpsGqkn6ZIxNCljEjQU');

INSERT INTO tables.users
(user_id, firstname, surname, email, verified, token, token_valid_until, token_type,password)
VALUES ('user_id_password_reset_3', 'Herbert', 'Mustermann', 'Herbert.Mustermann@example.de', true, 'token6', NOW() + INTERVAL '1 day', 'PASSWORD_RESET',
        '$argon2i$v=19$m=65536,t=2,p=1$by2LL/w2ShxPWgNLcxPiMQ$eu+b6YAWyzbrQx5io8hu6t3xGpsGqkn6ZIxNCljEjQU');

INSERT INTO tables.users
(user_id, firstname, surname, email, verified, token, token_valid_until, password)
VALUES ('user_id_token_invalid', 'Tim', 'Mustermann', 'Tim.Mustermann@example.de', false, 'token2', NOW(),
        '$argon2i$v=19$m=65536,t=2,p=1$by2LL/w2ShxPWgNLcxPiMQ$eu+b6YAWyzbrQx5io8hu6t3xGpsGqkn6ZIxNCljEjQU');

INSERT INTO tables.users (user_id, firstname, surname, email, verified, password)
VALUES ('user_id_verified', 'Theo', 'Mustermann', 'Theo.Mustermann@example.de', true,
        '$argon2i$v=19$m=65536,t=2,p=1$by2LL/w2ShxPWgNLcxPiMQ$eu+b6YAWyzbrQx5io8hu6t3xGpsGqkn6ZIxNCljEjQU');