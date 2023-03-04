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
import ru.yandex.practicum.filmorate.storage.query.QueryForFilms;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Qualifier("FilmDb")
public class FilmDb implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FilmGenresStorage filmGenresStorage;
    private String sqlQuery;

    @Autowired
    public FilmDb(JdbcTemplate jdbcTemplate, FilmGenresStorage filmGenresStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmGenresStorage = filmGenresStorage;
    }

    @Override
    public Film create(Film film) {
        long filmId = createFilm(film);
        film.setId(filmId);
        updateGenresInFilm(film);
        Film filmToReturn = getFilmById(filmId);
        log.info("Добавлен фильм :" + filmToReturn);
        return filmToReturn;
    }

    @Override
    public Film update(Film film) {
        log.info("Фильм :" + getFilmById(film.getId()) + " заменён на " + film);
        updateFilm(film);
        updateGenresInFilm(film);
        return getFilmById(film.getId());
    }

    @Override
    public List<Film> findAll() {
        sqlQuery = QueryForFilms.SELECT
                + QueryForFilms.FROM
                + QueryForFilms.GROUP_BY
                + QueryForFilms.ORDER_BY;

        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlQuery);
        return parseSqlRowToFilmList(rowSet);
    }

    @Override
    public Film getFilmById(long id) {
        sqlQuery = QueryForFilms.SELECT
                + QueryForFilms.FROM
                + QueryForFilms.WHERE_FILM_ID_EQUALS
                + QueryForFilms.GROUP_BY
                + QueryForFilms.ORDER_BY;

        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlQuery, id);
        List<Film> filmList = parseSqlRowToFilmList(rowSet);
        if (!filmList.isEmpty()) {
            return filmList.get(0);
        } else {
            return null;
        }
    }

    @Override
    public List<Film> getMostPopularFilms(int count) {
        List<Film> topFilms = new ArrayList<>();
        //Id популяпных фильмов
        sqlQuery = QueryForFilms.REQUEST_MOST_POPULAR;
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlQuery, count);
        while (rowSet.next()) {
            //Полная информация о каждом фильме
            long id = rowSet.getLong("FILM_ID");
            topFilms.add(getFilmById(id));
        }
        return topFilms;
    }

    @Override
    public void addLike(Long id, Long userId) {
        sqlQuery = QueryForFilms.INSERT_LIKE;
        jdbcTemplate.update(sqlQuery, id, userId);
    }

    @Override
    public void removeLike(Long id, Long userId) {
        sqlQuery = QueryForFilms.DELETE_LIKE;
        jdbcTemplate.update(sqlQuery, id, userId);
    }

    private long createFilm(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("FILMS").usingGeneratedKeyColumns("film_id");
        long filmId = simpleJdbcInsert.executeAndReturnKey(film.toMap()).longValue();
        if (film.getMpa() != null) {
            setMpaId(film.getMpa().getId(), filmId);
        }
        return filmId;
    }

    private void updateGenresInFilm(Film film) {
        filmGenresStorage.deleteAllGenres(film.getId());
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                filmGenresStorage.addGenreToFilm(film.getId(), genre.getId());
            }
        }
    }

    private void updateFilm(Film film) {
        sqlQuery = QueryForFilms.UPDATE_FILM;
        jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId(), film.getId());
    }

    private Film filmBuilder(SqlRowSet rowSet) {
        Film film;
        try {
            film = Film.builder()
                    .id(rowSet.getLong("FILM_ID"))
                    .name(rowSet.getString("NAME"))
                    .description(rowSet.getString("DESCRIPTION"))
                    .releaseDate(rowSet.getDate("RELEASE_DATE").toLocalDate())
                    .duration(rowSet.getLong("DURATION"))
                    .build();
        } catch (NullPointerException e) {
            film = null;
        }
        return film;
    }

    private void addMpaFromRowSet(SqlRowSet rowSet, Film film) {
        int mpaId = rowSet.getInt("MPA_ID");
        if (mpaId != 0) {
            Mpa mpa = Mpa.builder().id(mpaId).name(rowSet.getString("MPA_NAME")).build();
            film.setMpa(mpa);
        }
    }

    private void addGenreFromRowSet(SqlRowSet rowSet, Film film) {
        int genreId = rowSet.getInt("GENRE_ID");
        if (genreId != 0) {
            Genre genre = Genre.builder().id(genreId).name(rowSet.getString("GENRE_NAME")).build();
            film.addGenre(genre);
        }
    }

    private void addLikeFromRowSet(SqlRowSet rowSet, Film film) {
        long userId = rowSet.getLong("USER_ID");
        if (userId != 0) {
            film.addLike(userId);
        }
    }

    private List<Film> parseSqlRowToFilmList(SqlRowSet rowSet) {
        List<Film> filmList = new ArrayList<>();

        long filmId = 0L;
        int genreId = 0;
        long userId = 0L;
        Film film = null;

        while (rowSet.next()) {
            long newFilmId = rowSet.getLong("FILM_ID");
            int newGenreId = rowSet.getInt("GENRE_ID");
            long newUserId = rowSet.getLong("USER_ID");
            if (filmId != newFilmId) {
                //Новый фильм
                filmId = newFilmId;
                genreId = newGenreId;
                userId = newUserId;
                film = filmBuilder(rowSet);
                if (film != null) {
                    addMpaFromRowSet(rowSet, film);
                    addGenreFromRowSet(rowSet, film);
                    addLikeFromRowSet(rowSet, film);
                    filmList.add(film);
                }

            }
            if (genreId != newGenreId && film != null) {
                //Новый жанр
                genreId = newGenreId;
                userId = newUserId;
                addGenreFromRowSet(rowSet, film);
                addLikeFromRowSet(rowSet, film);
            }
            if (userId != newUserId && film != null) {
                //Новый лайк
                userId = newUserId;
                addLikeFromRowSet(rowSet, film);
            }
        }
        return filmList;
    }

    private void setMpaId(Integer mpaId, Long filmId) {
        sqlQuery = QueryForFilms.UPDATE_MPA_IN_FILM;
        jdbcTemplate.update(sqlQuery, mpaId, filmId);
    }
}



