package ru.yandex.practicum.filmorate.storage.imp.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmGenresStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.*;

@Slf4j
@Component
@Qualifier("FilmDb")
public class FilmDb implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FilmGenresStorage filmGenresStorage;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;
    private String sqlQuery;

    @Autowired
    public FilmDb(JdbcTemplate jdbcTemplate, FilmGenresStorage filmGenresStorage, MpaStorage mpaStorage, GenreStorage genreStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmGenresStorage = filmGenresStorage;
        this.mpaStorage = mpaStorage;
        this.genreStorage = genreStorage;
    }

    @Override
    public Film create(Film film) {
        long filmId = createFilm(film);
        film.setId(filmId);
        updateMpaAndGenres(film);
        log.info("Добавлен фильм :" + film);
        return film;
    }

    @Override
    public Film update(Film film) {
        log.info("Фильм :" + getFilmById(film.getId()) + " заменён на " + film);
        updateFilm(film);
        updateMpaAndGenres(film);
        return film;
    }

    @Override
    public Collection<Film> findAll() {
        List<Film> filmList = new ArrayList<>();

        sqlQuery = "select * from FILMS";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlQuery);
        if (rowSet.next()) {
            Film film = filmBuilder(rowSet);
            if (film != null) {
                filmList.add(film);
            }
        }
        return filmList;
    }

    @Override
    public Film getFilmById(long id) {
        Film film = null;
        sqlQuery = "select * from FILMS where FILM_ID = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (rowSet.next()) {
            film = filmBuilder(rowSet);
        }
        return film;
    }

    @Override
    public Collection<Film> getMostPopularFilms(int count) {
        List<Film> filmList = new ArrayList<>();
        sqlQuery = "select f.* from FILMS f left join LIKES l on f.FILM_ID = l.film_id group by f.film_id order by count(l.film_id) desc limit ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlQuery, count);
        if (rowSet.next()) {
            Film film = filmBuilder(rowSet);
            if (film != null) {
                filmList.add(film);
            }
        }
        return filmList;
    }

    @Override
    public void addLike(Long id, Long userId) {
        String sqlQuery = "insert into LIKES (FILM_ID,USER_ID) VALUES (?, ?);";
        jdbcTemplate.update(sqlQuery, id, userId);
        Film film = getFilmById(id);
        if (film != null){
            film.addLike(userId);
        }
    }

    @Override
    public void removeLike(Long id, Long userId) {
        String sqlQuery = "delete from LIKES where FILM_ID = ? and USER_ID = ?";
        jdbcTemplate.update(sqlQuery, id, userId);
        Film film = getFilmById(id);
        if (film != null){
            film.deleteLike(userId);
        }
    }

    private long createFilm(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("FILMS").usingGeneratedKeyColumns("film_id");
        return simpleJdbcInsert.executeAndReturnKey(film.toMap()).longValue();
    }

    private void setNameOfGenres(Film film) {
        Set<Genre> genres = new HashSet<>();
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                Genre genreWithName = genreStorage.getGenreById(genre.getId());
                genres.add(genreWithName);
            }
            film.setGenres(genres);
        }
    }

    private void updateGenresInFilm(Film film) {
        filmGenresStorage.deleteAllGenres(film.getId());
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                filmGenresStorage.addGenreToFilm(film.getId(), genre.getId());
            }
        }
    }

    private void setNameOfMpa(Film film) {
        if (film.getMpa() != null) {
            Mpa mpa = mpaStorage.getMpaById(film.getMpa().getId());
            film.setMpa(mpa);
        }
    }

    private void updateFilm(Film film) {
        sqlQuery = "update FILMS set NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, MPA_ID = ? WHERE FILM_ID = ?";
        jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId(), film.getId());
    }

    private void updateMpaAndGenres(Film film) {
        setNameOfGenres(film);
        updateGenresInFilm(film);
        setNameOfMpa(film);
    }

    private Film filmBuilder(SqlRowSet rowSet) {
        Film film;
        try {
            film =
                    Film.builder()
                            .id(rowSet.getLong("FILM_ID"))
                            .name(rowSet.getString("NAME"))
                            .description(rowSet.getString("DESCRIPTION"))
                            .releaseDate(rowSet.getDate("RELEASE_DATE").toLocalDate())
                            .duration(rowSet.getLong("DURATION"))
                            .mpa(Mpa.builder().id(rowSet.getInt("MPA_ID")).build())
                            .build();

            setNameOfMpa(film);
            film.setGenres(filmGenresStorage.getGenresByFilmId(film.getId()));

        } catch (NullPointerException e) {
            film = null;
        }
        return film;
    }
}



