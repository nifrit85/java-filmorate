package ru.yandex.practicum.filmorate.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.Exception.ValidationException;
import ru.yandex.practicum.filmorate.controller.validator.FilmValidator;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


@RestController
@Slf4j
public class FilmController {
    private int id = 1;
    Map<Integer, Film> films = new HashMap<>();

    @PostMapping(value = "/films")
    public Film create(@RequestBody Film film) throws ValidationException {
        if (FilmValidator.isValid(film)) {
            film.setId(id);
            id++;
            films.put(film.getId(), film);
            log.info("Добавлен фильм :" + film);
            return film;
        } else {
            log.error("Ошибка валидации фильма :" + film);
            throw new ValidationException("Некорректные данные о фильме");
        }
    }

    @PutMapping(value = "/films")
    public Film update(@RequestBody Film film) throws RuntimeException {
        if (FilmValidator.isValid(film)) {
            if (films.containsKey(film.getId())) {
                log.info("Фильм :" + films.get(film.getId()) + " заменён на " + film);
                films.put(film.getId(), film);
                return film;
            } else {
                log.error("Не найден фильм :" + film);
                throw new RuntimeException("Не найден фильм");
            }
        } else {
            log.error("Ошибка валидации фильма :" + film);
            throw new ValidationException("Некорректные данные о фильме");
        }
    }

    @GetMapping("/films")
    public Collection<Film> findAll() {
        return films.values();
    }
}
