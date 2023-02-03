package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.validator.FilmValidator;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private long id = 1;
    Map<Long, Film> films = new HashMap<>();

    @Override
    public Film create(Film film) {
        FilmValidator.isValid(film);

        film.setId(id);
        id++;
        films.put(film.getId(), film);
        log.info("Добавлен фильм :" + film);
        return film;
    }

    @Override
    public Film update(Film film) {
        FilmValidator.isValid(film);

        if (films.containsKey(film.getId())) {
            log.info("Фильм :" + films.get(film.getId()) + " заменён на " + film);
            films.put(film.getId(), film);
            return film;
        } else {
            log.error("Не найден фильм :" + film);
            throw new RuntimeException("Не найден фильм :" + film);
        }
    }

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }
}
