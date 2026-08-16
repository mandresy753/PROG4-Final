CREATE TABLE IF NOT EXISTS users
(
    id         UUID PRIMARY KEY,
    last_name  VARCHAR NOT NULL,
    first_name VARCHAR NOT NULL,
    email      VARCHAR NOT NULL UNIQUE,
    password   VARCHAR NOT NULL,
    role       VARCHAR NOT NULL,
    reference  VARCHAR(20) NOT NULL UNIQUE
    );