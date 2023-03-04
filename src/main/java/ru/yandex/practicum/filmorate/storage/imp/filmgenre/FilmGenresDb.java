package ru.yandex.practicum.filmorate.storage.imp.filmgenre;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.FilmGenresStorage;
import ru.yandex.practicum.filmorate.storage.query.QueryForFilmGenre;

@Component
public class FilmGenresDb implements FilmGenresStorage {

    private final JdbcTemplate jdbcTemplate;
    private String sqlQuery;

    @Autowired
    public FilmGenresDb(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void deleteAllGenres(Long filmId) {
        sqlQuery = QueryForFilmGenre.DELETE;
        jdbcTemplate.update(sqlQuery, filmId);
    }

    @Override
    public void addGenreToFilm(long filmId, int genreId) {
        sqlQuery = QueryForFilmGenre.INSERT;
        jdbcTemplate.update(sqlQuery, filmId, genreId);
    }
}
