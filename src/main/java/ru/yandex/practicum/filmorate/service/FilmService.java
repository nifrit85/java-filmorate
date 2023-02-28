package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.validator.FilmValidator;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;


@Slf4j
@Service

public class FilmService {

    private final FilmStorage storage;
    private final UserService userService;


    @Autowired
    public FilmService(@Qualifier("FilmDb") FilmStorage storage, UserService service) {
        this.storage = storage;
        this.userService = service;
    }

    public void addLike(long filmId, long userId) {
        storage.addLike(filmId,userId);
    }

    public void deleteLike(long filmId, long userId) {
        storage.removeLike(filmId,userId);
    }

    public Collection<Film> getMostPopularFilms(int count) {
        return storage.getMostPopularFilms(count);
    }

    public Film create(Film film) {
        FilmValidator.isValid(film);
        return storage.create(film);
    }

    public Film update(Film film) {
        FilmValidator.isValid(film);
        getFilmById(film.getId()); //Проверка на существование. Выдаёт исключение
        return storage.update(film);
    }

    public Collection<Film> findAll() {
        return storage.findAll();
    }

    public Film getFilmById(long id) {
        Film film = storage.getFilmById(id);
        if (film == null) {
            throw new NotFoundException("Фильм", id);
        }
        return film;
    }
}
