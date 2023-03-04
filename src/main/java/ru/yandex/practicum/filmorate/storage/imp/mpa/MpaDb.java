package ru.yandex.practicum.filmorate.storage.imp.mpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.storage.query.QueryForMpa;

import java.util.ArrayList;
import java.util.List;

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
        sqlQuery = QueryForMpa.SELECT_BY_ID;
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (rowSet.next()) {
            return mpaBuilder(rowSet);
        } else {
            return null;
        }
    }

    @Override
    public List<Mpa> findAll() {
        List<Mpa> mpaList = new ArrayList<>();

        sqlQuery = QueryForMpa.SELECT_ALL;
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlQuery);
        while (rowSet.next()) {
            Mpa mpa = mpaBuilder(rowSet);
            if (mpa != null) {
                mpaList.add(mpa);
            }
        }
        return mpaList;
    }

    private Mpa mpaBuilder(SqlRowSet rowSet) {
        return Mpa.builder()
                .id(rowSet.getInt("MPA_ID"))
                .name(rowSet.getString("NAME"))
                .build();
    }
}
