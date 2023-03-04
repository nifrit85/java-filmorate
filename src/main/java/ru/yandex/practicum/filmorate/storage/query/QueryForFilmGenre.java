package ru.yandex.practicum.filmorate.storage.query;

public class QueryForFilmGenre {
    public static final String DELETE =
            "delete from FILM_GENRES where FILM_ID = ? ";

    public static final String INSERT =
            "insert into FILM_GENRES(FILM_ID, GENRE_ID) values(?,?) ";
}
