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
import java.util.stream.Collectors;

@Slf4j
@Component
@Qualifier("FilmDb")
public class FilmDb implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FilmGenresStorage filmGenresStorage;

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
        return getFilmById(filmId);
    }

    @Override
    public Film update(Film film) {
        updateFilm(film);
        updateGenresInFilm(film);
        return getFilmById(film.getId());
    }

    @Override
    public List<Film> findAll() {
        String sqlQuery = QueryForFilms.SELECT
                        + QueryForFilms.FROM
                        + QueryForFilms.GROUP_BY
                        + QueryForFilms.ORDER_BY;

        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlQuery);
        return parseSqlRowToFilmList(rowSet);
    }

    @Override
    public Film getFilmById(long id) {
        String sqlQuery = QueryForFilms.SELECT
                        + QueryForFilms.FROM
                        + QueryForFilms.WHERE_FILM_ID_EQUALS
                        + QueryForFilms.GROUP_BY
                        + QueryForFilms.ORDER_BY;

        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlQuery, id);
        List<Film> filmList = parseSqlRowToFilmList(rowSet);
        if (!filmList.isEmpty()) {
            log.debug("Фильм с ID: " + id + " успешно найден");
            return filmList.get(0);
        } else {
            log.debug("Фильм с ID: " + id + " не найден в БД");
            return null;
        }
    }

    @Override
    public List<Film> getMostPopularFilms(int count) {
        List<Film> topFilms = new ArrayList<>();
        //Id популяпных фильмов
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(QueryForFilms.REQUEST_MOST_POPULAR, count);
        while (rowSet.next()) {
            //Полная информация о каждом фильме
            long id = rowSet.getLong("FILM_ID");
            topFilms.add(getFilmById(id));
        }
        return topFilms;
    }

    @Override
    public void addLike(Long id, Long userId) {
        jdbcTemplate.update(QueryForFilms.INSERT_LIKE, id, userId);
        log.debug("Фильму с ID: " + id + " добавлен лайк от пользователя с ID: " + userId);
    }

    @Override
    public void removeLike(Long id, Long userId) {
        jdbcTemplate.update(QueryForFilms.DELETE_LIKE, id, userId);
        log.debug("У фильма с ID: " + id + " удалён лайк от пользователя с ID: " + userId);
    }

    private long createFilm(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("FILMS").usingGeneratedKeyColumns("film_id");
        long filmId = simpleJdbcInsert.executeAndReturnKey(film.toMap()).longValue();
        log.debug("Фильм с ID: " + filmId + " успешно добавлен в БД");
        if (film.getMpa() != null) {
            setMpaId(film.getMpa().getId(), filmId);
        }
        return filmId;
    }

    private void updateGenresInFilm(Film film) {
        filmGenresStorage.deleteAllGenres(film.getId());

        if (film.getGenres() != null) {
            List<Integer> genreList = film.getGenres().stream().map(Genre::getId).collect(Collectors.toList());
            filmGenresStorage.addGenresToFilm(film.getId(),genreList);
        }
    }

    private void updateFilm(Film film) {
        jdbcTemplate.update(QueryForFilms.UPDATE_FILM, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId(), film.getId());
        log.debug("Фильм с ID: " + film.getId() + " успешно обновлён в БД");
    }

    private Film filmBuilder(SqlRowSet rowSet) {
        Film film = Film.builder()
                .id(rowSet.getLong("FILM_ID"))
                .name(rowSet.getString("NAME"))
                .description(rowSet.getString("DESCRIPTION"))
                .duration(rowSet.getLong("DURATION"))
                .build();

        if (rowSet.getDate("RELEASE_DATE") != null && film != null) {
            film.setReleaseDate(rowSet.getDate("RELEASE_DATE").toLocalDate());
        }else if(rowSet.getDate("RELEASE_DATE") == null ){
            log.debug("Ошибка получения даты релиза для фильма с ID: " + rowSet.getLong("FILM_ID"));
        }else if (film == null){
            log.debug("Ошибка создания фильма :" + rowSet);
        }

        return film;
    }

    private void addMpaFromRowSet(SqlRowSet rowSet, Film film) {
        int mpaId = rowSet.getInt("MPA_ID");
        if (mpaId != 0) {
            Mpa mpa = Mpa.builder()
                    .id(mpaId)
                    .name(rowSet.getString("MPA_NAME"))
                    .build();
            film.setMpa(mpa);
            log.debug("Фильму с ID: " + film.getId() + " установлен рейтинг: " + mpa.getName());
        }
    }

    private void addGenreFromRowSet(SqlRowSet rowSet, Film film) {
        int genreId = rowSet.getInt("GENRE_ID");
        if (genreId != 0) {
            Genre genre = Genre.builder()
                    .id(genreId)
                    .name(rowSet.getString("GENRE_NAME"))
                    .build();
            film.addGenre(genre);
            log.debug("Фильму с ID: " + film.getId() + " добавлен жанр: " + genre.getName());
        }
    }

    private void addLikeFromRowSet(SqlRowSet rowSet, Film film) {
        long userId = rowSet.getLong("USER_ID");
        if (userId != 0) {
            film.addLike(userId);
            log.debug("Фильму с ID: " + film.getId() + " добавлен лайк от пользователя с ID: " + userId);
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
                }else {
                    log.debug("Ошибка создания фильма :" + rowSet);
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
        jdbcTemplate.update(QueryForFilms.UPDATE_MPA_IN_FILM, mpaId, filmId);
        log.debug("Рейтинг ID: " + mpaId + " добавлен фильму ID: " + filmId);
    }
}



