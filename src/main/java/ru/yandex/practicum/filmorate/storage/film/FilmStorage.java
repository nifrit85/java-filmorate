package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.Collection;

@RestController
public interface FilmStorage {

    @PostMapping(value = "/films")
    Film create(@Valid @RequestBody Film film);

    @PutMapping(value = "/films")
    Film update(@Valid @RequestBody Film film);

    @GetMapping("/films")
    Collection<Film> findAll();
}
