package ru.yandex.practicum.filmorate.storage.imp.genre;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.query.QueryForGenre;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class GenreDb implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDb(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Genre getGenreById(Integer id) {
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(QueryForGenre.SELECT_BY_ID, id);
        if (rowSet.next()) {
            log.debug("Жанр с ID: " + id + " успешно найден");
            return genreBuilder(rowSet);
        } else {
            log.debug("Жанр с ID: " + id + " не найден в БД");
            return null;
        }
    }

    @Override
    public List<Genre> findAll() {
        List<Genre> genreList = new ArrayList<>();
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(QueryForGenre.SELECT_ALL);
        while (rowSet.next()) {
            Genre genre = genreBuilder(rowSet);
            if (genre != null) {
                genreList.add(genre);
            }else {
                log.debug("Ошибка создания жанра :" + rowSet);
            }
        }
        return genreList;
    }

    private Genre genreBuilder(SqlRowSet rowSet) {
        return Genre.builder()
                .id(rowSet.getInt("GENRE_ID"))
                .name(rowSet.getString("NAME"))
                .build();
    }
}
