package ru.yandex.practicum.filmorate.service.validator;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

@Slf4j
public class FilmValidator {
    public static void isValid(Film film) throws ValidationException {
        if (film == null) {
            log.error("Ошибка валидации фильма : Заполните данные о фильме. (null)");
            throw new ValidationException("Заполните данные о фильме");
        }
        if (film.getReleaseDate() != null
                && film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("Ошибка валидации фильма : Дата релиза не может быть раньше 28 декабря 1895 года. Данные фильма: " + film);
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
    }
}
