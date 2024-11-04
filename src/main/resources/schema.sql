drop table if exists bookings;
drop table if exists comments;
drop table if exists items;
drop table if exists requests;
-- DROP TABLE IF EXISTS users CASCADE;
drop table if exists users;

create table if not exists users
(
    id    serial8,
    name  varchar not null,
    email varchar not null unique,
    primary key (id)
);

create table if not exists requests
(
    id          serial8,
    user_id     int8      not null,
    description varchar   not null,
    created     timestamp without time zone not null,
    primary key (id),
    foreign key (user_id) references users (id)
);

create table if not exists items
(
    id          serial8,
    name        varchar not null,
    description varchar not null,
    available   boolean not null,
    user_id     int8    not null,
    request_id int8,
    primary key (id),
    foreign key (user_id) references users (id),
    foreign key (request_id) references requests (id)
);

create table if not exists bookings
(
    id        serial8,
    user_id   int8      not null, -- бронирующий пользователь
    item_id   int8      not null,
    status    int2      not null, -- статус enum
    from_date timestamp without time zone not null,
    to_date   timestamp without time zone not null,
    primary key (id),
    foreign key (user_id) references users (id),
    foreign key (item_id) references items (id)
);

create table if not exists comments
(
    id           serial8,
    user_id      int8    not null,
    item_id      int8    not null,
    text         varchar not null,
    comment_date date    not null,
    primary key (id),
    foreign key (user_id) references users (id),
    foreign key (item_id) references items (id)
);