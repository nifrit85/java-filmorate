package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTest {
   static private String[] agrs = new String[]{};
   static private ConfigurableApplicationContext ctx;


   private Film film;
   private FilmController controller;
    @BeforeAll
    static void beforeAll(){
//        ctx = SpringApplication.run(FilmorateApplication.class, agrs);
    }
    @AfterAll
    static void AfterAll(){
//        ctx.close();
    }
    @BeforeEach
    void beforeEach() {
        film = new Film(1,"Good film","Very good film",LocalDate.of(2023, 1, 16),95L);
        controller = new FilmController();
    }
    @Test
    void testPostGood(){
        controller.create(film);
        assertTrue(controller.findAll().contains(film));
    }
}
