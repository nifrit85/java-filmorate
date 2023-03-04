merge INTO GENRES (genre_id, name)
    VALUES (1, 'Комедия'),
           (2, 'Драма'),
           (3, 'Мультфильм'),
           (4, 'Триллер'),
           (5, 'Документальный'),
           (6, 'Боевик');

merge INTO MPA (mpa_id, name)
    VALUES (1, 'G'),
           (2, 'PG'),
           (3, 'PG-13'),
           (4, 'R'),
           (5, 'NC-17');

insert into FILMS (name, description, release_date, duration, mpa_id)
values ('Name1', 'Desc1', '1990-01-01', 10, 1),
       ('Name2', 'Desc2', '1990-01-02', 20, 2),
       ('Name3', 'Desc3', '1990-01-03', 30, 3);

insert into USERS (EMAIL, LOGIN, NAME, BIRTHDAY)
values ('u1@yandex.ru', 'u1', 'User1', '2000-01-01'),
       ('u2@yandex.ru', 'u2', 'User2', '2001-01-01'),
       ('u3@yandex.ru', 'u3', 'User3', '2002-01-01'),
       ('u4@yandex.ru', 'u4', 'User4', '2003-01-01'),
       ('u5@yandex.ru', 'u5', 'User5', '2004-01-01');

insert into FILM_GENRES (FILM_ID, GENRE_ID)
values (1, 1),
       (1, 2),
       (2, 2),
       (2, 3),
       (3, 4),
       (3, 5),
       (3, 6);

insert into LIKES (FILM_ID, USER_ID)
values (3, 1),
       (3, 2),
       (3, 3),
       (1, 1),
       (1, 2),
       (2, 3);

insert into FRIENDSHIP (USER_ID, FRIEND_ID, IS_CONFIRMED)
values (1, 2, false),
       (1, 3, true),
       (4, 5, true),
       (5, 3, false);


