package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;
import java.util.logging.Level;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        Film filmToReturn = filmService.create(film);
        log.info("Добавлен фильм :" + filmToReturn.toString());
        return filmToReturn;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        Film filmToReturn = filmService.update(film);
        log.info("Фильм :" + film.toString() + " заменён на " + filmToReturn.toString());
        return filmToReturn;
    }

    @GetMapping
    public List<Film> findAll() {
        log.info("Возвращаем список фильмов");
        return filmService.findAll();
    }

    @GetMapping("{id}")
    public Film getFilmById(@PathVariable long id) {
        log.info("Получаем фильм с ID: " + id);
        return filmService.getFilmById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable("id") long filmId, @PathVariable long userId) {
        log.info("Для фильма с ID: " + filmId + " добавляем лайк от пользователя c ID: " + userId);
        filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") long filmId, @PathVariable long userId) {
        log.info("Для фильма с ID: " + filmId + " удаляем лайк от пользователя c ID: " + userId);
        filmService.deleteLike(filmId, userId);
    }

    @GetMapping("/popular")
    public List<Film> getMostPopularFilms(@RequestParam(required = false, defaultValue = "10") Integer count) {
        log.info("Получаем " + count + " популярных фильмов");
        return filmService.getMostPopularFilms(count);
    }
}



