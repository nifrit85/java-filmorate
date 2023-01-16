package ru.yandex.practicum.filmorate.controller.validator;

import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

public class FilmValidator {
    public static boolean isValid(Film film) {
        if (film.getName() == null || film.getName().isBlank() ||
                film.getDescription() == null || film.getDescription().length() > 200 ||
                film.getReleaseDate() == null || film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28)) ||
                film.getDuration() == null || film.getDuration() < 1)
            return false;
        return true;
    }
}
