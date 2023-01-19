package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {

    private Validator validator;

    private Film film;
    private FilmController controller;
    @BeforeEach
    void beforeEach() {
        film = Film.builder()
                .name("Good film")
                .description("Very good film")
                .releaseDate(LocalDate.of(2023, 1, 16))
                .duration(95L)
                .build();
        controller = new FilmController();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }
    @Test
    void testPostGood(){
        controller.create(film);
        assertTrue(controller.findAll().contains(film));
    }
    @Test
    void testPostNull(){
        //Если фильм не передали - ошибка валидации
        Assertions.assertThrows(ValidationException.class, () -> controller.create(null));
    }
    @Test
    void testPutGood(){
        controller.create(film);
        assertTrue(controller.findAll().contains(film));

        Film filmToUpdate = Film.builder()
                .id(film.getId())
                .name("Good film update")
                .description("Very good film updated")
                .releaseDate(film.getReleaseDate().plusDays(1))
                .duration(96L)
                .build();
        controller.update(filmToUpdate);
        //Фильм один
        assertEquals(1, controller.findAll().size());
        //Обновлённый фильм
        assertTrue(controller.findAll().contains(filmToUpdate));
    }
    @Test
    void testPutNull(){
        //Если фильм не передали - ошибка валидации
        Assertions.assertThrows(ValidationException.class, () -> controller.update(null));
    }

    @Test
    void e(){
        film.setName(" ");
        Set<ConstraintViolation<Film>> violations = validator.validate(null);
        assertFalse(violations.isEmpty());
    }
}