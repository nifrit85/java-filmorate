package ru.yandex.practicum.filmorate.storage.imp.mpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

@Component
public class MpaDb implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;
    private String sqlQuery;

    @Autowired
    public MpaDb(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Mpa getMpaById(Integer id) {
        sqlQuery = "select * from MPA where MPA_ID = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (rowSet.next()) {
            return Mpa.builder().id(rowSet.getInt("MPA_ID")).name(rowSet.getString("NAME")).build();
        } else {
            return null;
        }
    }
}
