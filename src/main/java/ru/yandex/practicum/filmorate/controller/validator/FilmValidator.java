package ru.yandex.practicum.filmorate.controller.validator;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import java.time.LocalDate;

public class FilmValidator {
    public static void isValid(Film film) throws ValidationException {
        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28)))
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
    }
}
