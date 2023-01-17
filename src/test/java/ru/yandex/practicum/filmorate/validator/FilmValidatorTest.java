package ru.yandex.practicum.filmorate.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.validator.FilmValidator;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


class FilmValidatorTest {
    private Film film;

    @BeforeEach
    void beforeEach() {
        film = Film.builder()
                .name("Good film")
                .description("Very good film")
                .releaseDate(LocalDate.of(2023, 1, 16))
                .duration(95L)
                .build();
    }

    @Test
    void testGoodFilm() {
        assertTrue(FilmValidator.isValid(film));
        //Описание 200 символов
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 200; i++) {
            stringBuilder.append("Q");
        }
        film.setDescription(stringBuilder.toString());
        assertTrue(FilmValidator.isValid(film));
        //дата релиза —  28 декабря 1895 года
        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        assertTrue(FilmValidator.isValid(film));
    }

    @Test
    void testBadFilmName() {
        //название не может быть пустым;
        film.setName("");
        assertFalse(FilmValidator.isValid(film));
        film.setName("   ");
        assertFalse(FilmValidator.isValid(film));
        film.setName(null);
        assertFalse(FilmValidator.isValid(film));
    }

    @Test
    void testBadFilmDescription() {
        //максимальная длина описания — 200 символов
        film.setDescription(null);
        assertFalse(FilmValidator.isValid(film));
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 201; i++) {
            stringBuilder.append("Q");
        }
        film.setDescription(stringBuilder.toString());
        assertFalse(FilmValidator.isValid(film));
    }

    @Test
    void testBadFilmReleaseDate() {
        //дата релиза — не раньше 28 декабря 1895 года
        film.setReleaseDate(null);
        assertFalse(FilmValidator.isValid(film));

        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        assertFalse(FilmValidator.isValid(film));
    }

    @Test
    void testBadFilmDuration() {
        //продолжительность фильма должна быть положительной
        film.setDuration(null);
        assertFalse(FilmValidator.isValid(film));
        // Ноль - не положительное число
        film.setDuration(0L);
        assertFalse(FilmValidator.isValid(film));

        film.setDuration(-1L);
        assertFalse(FilmValidator.isValid(film));
    }
}
