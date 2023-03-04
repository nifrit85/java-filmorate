package ru.yandex.practicum.filmorate.storage.query;

public class QueryForFilms {
    public static final String SELECT =
            "select f.FILM_ID, "
                    + "f.NAME, "
                    + "f.DESCRIPTION, "
                    + "f.RELEASE_DATE, "
                    + "f.DURATION, "
                    + "m.MPA_ID, "
                    + "m.NAME as mpa_name, "
                    + "g2.GENRE_ID, "
                    + "g2.NAME as genre_name, "
                    + "u.USER_ID, ";

    public static final String FROM =
            "from FILMS f "
                    + "left join MPA m on f.MPA_ID = m.MPA_ID "
                    + "left join FILM_GENRES fg on f.FILM_ID = fg.FILM_ID "
                    + "left join GENRES g2 on g2.GENRE_ID = fg.GENRE_ID "
                    + "left join LIKES l on f.FILM_ID = l.FILM_ID "
                    + "left join USERS u on l.USER_ID = u.USER_ID ";

    public static final String GROUP_BY = "group by f.FILM_ID, g2.GENRE_ID, l.USER_ID ";

    public static final String ORDER_BY = "order by f.FILM_ID, g2.GENRE_ID, l.USER_ID ";

    public static final String WHERE_FILM_ID_EQUALS = "where f.FILM_ID = ? ";

    public static final String REQUEST_MOST_POPULAR =
            "select f.FILM_ID "
                    + "from FILMS f "
                    + "left join LIKES l on f.FILM_ID = l.FILM_ID "
                    + "group by f.film_id "
                    + "order by count(l.film_id) desc limit ? ";

    public static final String INSERT_LIKE = "insert into LIKES (FILM_ID,USER_ID) VALUES (?, ?) ";

    public static final String DELETE_LIKE = "delete from LIKES where FILM_ID = ? and USER_ID = ? ";

    public static final String UPDATE_FILM = "update FILMS set NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, MPA_ID = ? WHERE FILM_ID = ? ";

    public static final String UPDATE_MPA_IN_FILM = "update FILMS set MPA_ID = ? WHERE FILM_ID = ? ";
}
