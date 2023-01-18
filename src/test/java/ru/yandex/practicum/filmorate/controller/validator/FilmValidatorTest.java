package ru.yandex.practicum.filmorate.controller.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

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
        //Неи исключения при отсутствии ошибок
        assertDoesNotThrow(()->FilmValidator.isValid(film));
    }

    @Test
    void testGoodReleaseDate() {
        //дата релиза —  28 декабря 1895 года
        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        assertDoesNotThrow(()->FilmValidator.isValid(film));
    }

    @Test
    void testBadReleaseDate() {
        //дата релиза —  27 декабря 1895 года
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        assertThrows(ValidationException.class,()->FilmValidator.isValid(film));
    }
}
