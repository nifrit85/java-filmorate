package ru.yandex.practicum.filmorate.storage.imp.filmGenre;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmGenresStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.HashSet;
import java.util.Set;

@Component
public class FilmGenresDb implements FilmGenresStorage {

    private final JdbcTemplate jdbcTemplate;
    private final GenreStorage genreStorage;
    private String sqlQuery;

    @Autowired
    public FilmGenresDb(JdbcTemplate jdbcTemplate, GenreStorage genreStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreStorage = genreStorage;
    }


    @Override
    public void deleteAllGenres(Long filmId) {
        sqlQuery = "delete from FILM_GENRES where FILM_ID = ?";
        jdbcTemplate.update(sqlQuery, filmId);
    }

    @Override
    public void addGenreToFilm(long filmId, int genreId) {
        sqlQuery = "insert into FILM_GENRES(FILM_ID, GENRE_ID) values(?,?)";
        jdbcTemplate.update(sqlQuery, filmId, genreId);
    }

    @Override
    public Set<Genre> getGenresByFilmId(long id) {
        Set<Genre> genres = new HashSet<>();
        sqlQuery = "select * from FILM_GENRES where FILM_ID = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (rowSet.next()) {
            Genre genre = genreStorage.getGenreById(rowSet.getInt("GENRE_ID"));
            genres.add(genre);
        }
        return genres;
    }
}
