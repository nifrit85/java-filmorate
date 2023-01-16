package ru.yandex.practicum.filmorate.controller.validator;

import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

public class FilmValidator {
    public static boolean isValid(Film film) {
        if ((film.getName().isBlank()) || (film.getDescription().length() > 200) || (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) || (film.getDuration() < 0))
            return false;
        return true;
    }
}
