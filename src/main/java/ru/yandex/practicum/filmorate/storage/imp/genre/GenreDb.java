package ru.yandex.practicum.filmorate.storage.imp.genre;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

@Component
public class GenreDb implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;
    private String sqlQuery;

    @Autowired
    public GenreDb(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Genre getGenreById(Integer id) {
        sqlQuery = "SELECT * FROM GENRES WHERE GENRE_ID = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (rowSet.next()) {
            return Genre.builder()
                    .id(rowSet.getInt("GENRE_ID"))
                    .name(rowSet.getString("NAME"))
                    .build();
        } else {
            return null;
        }
    }
}
