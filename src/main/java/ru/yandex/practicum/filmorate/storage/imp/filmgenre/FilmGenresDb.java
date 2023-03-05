package ru.yandex.practicum.filmorate.storage.imp.filmgenre;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.FilmGenresStorage;
import ru.yandex.practicum.filmorate.storage.query.QueryForFilmGenre;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Component
@Slf4j
public class FilmGenresDb implements FilmGenresStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmGenresDb(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void deleteAllGenres(Long filmId) {
        jdbcTemplate.update(QueryForFilmGenre.DELETE, filmId);
        log.debug("Очищен список жанров у фильма с ID " + filmId);
    }

    @Override
    public void addGenreToFilm(long filmId, int genreId) {
        jdbcTemplate.update(QueryForFilmGenre.INSERT, filmId, genreId);
        log.debug("Жанр с ID: " + genreId + " добавлены фильму ID: " + filmId);
    }

    @Override
    public void addGenresToFilm(long filmId, List<Integer> genresId) {
        jdbcTemplate.batchUpdate(QueryForFilmGenre.INSERT,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setLong(1, filmId);
                        ps.setInt(2,genresId.get(i));
                    }

                    @Override
                    public int getBatchSize() {
                        return genresId.size();
                    }
                });

        log.debug("Жанры (ID): " + genresId.toString() + " добавлены фильму c ID: " + filmId);
    }
}
