create table if not exists MPA
(
    mpa_id int generated by default as identity primary key,
    name   varchar
);

create table if not exists Films
(
    film_id     bigint generated by default as identity primary key,
    name        varchar not null,
    description varchar(200),
    release_date date,
    duration    int,
    mpa_id      int references MPA (mpa_id),
    constraint name_not_blank check (name <> ''),
    constraint duration_is_positive check (duration is null or duration > 0)
);

create table if not exists Genres
(
    genre_id int generated by default as identity primary key,
    name     varchar not null
);

create table if not exists Film_genres
(
    film_genres_id int generated by default as identity primary key,
    film_id        bigint references Films (film_id),
    genre_id       int references Genres (genre_id)
);

create table if not exists Users
(
    user_id  int generated by default as identity primary key,
    email    varchar,
    login    varchar,
    name     varchar,
    birthday date
    constraint email_not_blank check (email <> ' '),
    constraint login_not_blank check (login <> ' '),
    constraint birthday_in_past check (birthday is null or birthday < SYSDATE)
);

create table if not exists Friendship
(
    friendship_id bigint generated by default as identity primary key,
    user_id       bigint references Users (user_id),
    friend_id     bigint references Users (user_id),
    is_confirmed  boolean default false
);

create table if not exists Likes
(
    likes_id bigint generated by default as identity primary key,
    film_id  bigint references Films (film_id),
    user_id  bigint references Users (user_id)
)

