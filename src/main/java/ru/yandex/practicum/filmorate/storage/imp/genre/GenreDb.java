package ru.yandex.practicum.filmorate.storage.imp.genre;

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
public class GenreDb implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;
    private String sqlQuery;

    @Autowired
    public GenreDb(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Genre getGenreById(Integer id) {
        sqlQuery = QueryForGenre.SELECT_BY_ID;
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (rowSet.next()) {
            return genreBuilder(rowSet);
        } else {
            return null;
        }
    }

    @Override
    public List<Genre> findAll() {
        List<Genre> genreList = new ArrayList<>();

        sqlQuery = QueryForGenre.SELECT_ALL;
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlQuery);
        while (rowSet.next()) {
            Genre genre = genreBuilder(rowSet);
            if (genre != null) {
                genreList.add(genre);
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
