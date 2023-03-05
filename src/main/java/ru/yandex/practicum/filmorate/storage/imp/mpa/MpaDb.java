package ru.yandex.practicum.filmorate.storage.imp.mpa;

import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class MpaDb implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaDb(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Mpa getMpaById(Integer id) {
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(QueryForMpa.SELECT_BY_ID, id);
        if (rowSet.next()) {
            log.debug("Рейтинг с ID: " + id + " успешно найден");
            return mpaBuilder(rowSet);
        } else {
            log.debug("Рейтинг с ID: " + id + " не найден в БД");
            return null;
        }
    }

    @Override
    public List<Mpa> findAll() {
        List<Mpa> mpaList = new ArrayList<>();
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(QueryForMpa.SELECT_ALL);
        while (rowSet.next()) {
            Mpa mpa = mpaBuilder(rowSet);
            if (mpa != null) {
                mpaList.add(mpa);
            }else {
                log.debug("Ошибка создания рейтинг :" + rowSet);
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
