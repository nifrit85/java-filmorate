package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserControllerTest {
    private User user;
    private UserController controller;
    @BeforeEach
    void beforeEach() {
        user = User.builder()
                .email("yandex@yandex.ru")
                .login("yandex")
                .name("Аркадий Волож")
                .birthday(LocalDate.of(1964,2,11))
                .build();
        controller = new UserController();
    }
    @Test
    void testPostGood(){
        controller.create(user);
        assertTrue(controller.findAll().contains(user));
    }
    @Test
    void testPostNull(){
        //Если пользователя не передали - ошибка валидации
        Assertions.assertThrows(ValidationException.class, () -> controller.create(null));
    }
    @Test
    void testPutGood(){
        controller.create(user);
        assertTrue(controller.findAll().contains(user));

        User userToUpdate = User.builder()
                .id(user.getId())
                .email("yandex1@yandex.ru")
                .login("yandex1")
                .name("Аркадий Волож1")
                .birthday(LocalDate.of(1964,2,11))
                .build();
        controller.update(userToUpdate);
        //Пользователь один
        assertEquals(1, controller.findAll().size());
        //Обновлённый пользователь
        assertTrue(controller.findAll().contains(userToUpdate));
    }
    @Test
    void testPutNull(){
        //Если пользователя не передали - ошибка валидации
        Assertions.assertThrows(ValidationException.class, () -> controller.update(null));
    }
}