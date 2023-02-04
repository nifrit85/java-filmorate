package ru.yandex.practicum.filmorate.controller.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.validator.FilmValidator;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FilmValidatorTest {
    private Film film;
    private Validator validator;
    private Set<ConstraintViolation<Film>> violations;

    @BeforeEach
    void beforeEach() {
        film = Film.builder().name("Good film").description("Very good film").releaseDate(LocalDate.of(2023, 1, 16)).duration(95L).build();

        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.usingContext().getValidator();
    }

    @Test
    void testGoodFilm() {
        //Неи исключения при отсутствии ошибок
        assertDoesNotThrow(() -> FilmValidator.isValid(film));
    }

    @Test
    void testGoodReleaseDate() {
        //дата релиза —  28 декабря 1895 года
        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        assertDoesNotThrow(() -> FilmValidator.isValid(film));
    }

    @Test
    void testBadReleaseDate() {
        //дата релиза —  27 декабря 1895 года
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        assertThrows(ValidationException.class, () -> FilmValidator.isValid(film));
    }

    @Test
    void testBadName() {
        //Название не может быть пустым
        film.setName("");
        violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        violations.stream().map(ConstraintViolation::getMessage).forEach(System.out::println);

        film.setName("   ");
        violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        violations.stream().map(ConstraintViolation::getMessage).forEach(System.out::println);

        film.setName(null);
        violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        violations.stream().map(ConstraintViolation::getMessage).forEach(System.out::println);
    }

    @Test
    void testGoodDescription() {
        //Максимальная длина описания — 200 символов
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 200; i++) {
            stringBuilder.append("Q");
        }
        film.setDescription(stringBuilder.toString());
        violations = validator.validate(film);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testBadDescription() {
        //Максимальная длина описания — 200 символов
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 201; i++) {
            stringBuilder.append("Q");
        }
        film.setDescription(stringBuilder.toString());
        violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        violations.stream().map(ConstraintViolation::getMessage).forEach(System.out::println);
    }

    @Test
    void testBadDuration() {
        //Продолжительность фильма должна быть положительной
        film.setDuration(-1L);
        violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        violations.stream().map(ConstraintViolation::getMessage).forEach(System.out::println);

        film.setDuration(0L);
        violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        violations.stream().map(ConstraintViolation::getMessage).forEach(System.out::println);
    }
}
