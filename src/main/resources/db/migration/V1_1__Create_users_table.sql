create table if not exists users
(
    id         uuid
        constraint users_pk primary key,
    last_name  varchar not null,
    first_name varchar not null,
    email      varchar not null
        constraint users_email_uk unique,
    password   varchar not null,
    role       varchar not null
);
