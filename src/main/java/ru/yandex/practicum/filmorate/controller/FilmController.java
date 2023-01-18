package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.controller.validator.FilmValidator;
import ru.yandex.practicum.filmorate.model.Film;
import javax.validation.Valid;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
public class FilmController {
    private long id = 1;
    Map<Long, Film> films = new HashMap<>();

    @PostMapping(value = "/films")
    public Film create(@Valid @RequestBody Film film) {
        try {
            FilmValidator.isValid(film);
        } catch (ValidationException e) {
            log.error("Ошибка валидации фильма :" + e.getMessage() + " Данные фильма: " + film);
            throw e;
        }
        film.setId(id);
        id++;
        films.put(film.getId(), film);
        log.info("Добавлен фильм :" + film);
        return film;
    }

    @PutMapping(value = "/films")
    public Film update(@Valid @RequestBody Film film) {
        try {
            FilmValidator.isValid(film);
        } catch (ValidationException e) {
            log.error("Ошибка валидации фильма :" + e.getMessage() + " Данные фильма: " + film);
            throw e;
        }

        if (films.containsKey(film.getId())) {
            log.info("Фильм :" + films.get(film.getId()) + " заменён на " + film);
            films.put(film.getId(), film);
            return film;
        } else {
            log.error("Не найден фильм :" + film);
            throw new RuntimeException("Не найден фильм :" + film);
        }
    }

    @GetMapping("/films")
    public Collection<Film> findAll() {
        return films.values();
    }
}
