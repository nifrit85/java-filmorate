package ru.yandex.practicum.filmorate.storage.query;

public class QueryForGenre {
    public static final String SELECT_BY_ID =
            "SELECT * FROM GENRES WHERE GENRE_ID = ? ";

    public static final String SELECT_ALL =
            "select * from GENRES ";
}
