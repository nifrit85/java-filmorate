package ru.yandex.practicum.filmorate.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.validator.UserValidator;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class UserValidatorTest {
    private User user;

    @BeforeEach
    void beforeEach(){
        user = new User(1,"yandex@yandex.ru","yandex","Аркадий Волож",LocalDate.of(1964,02,11));
    }
    @Test
    void testGoodUser() {
        assertTrue(UserValidator.isValid(user));
        //С днём рождения!
        user.setBirthday(LocalDate.now());
        assertTrue(UserValidator.isValid(user));
    }
    @Test
    void testBadUserEmail() {
        //электронная почта не может быть пустой и должна содержать символ @
        user.setEmail(null);
        assertFalse(UserValidator.isValid(user));
        user.setEmail("");
        assertFalse(UserValidator.isValid(user));
        user.setEmail("  ");
        assertFalse(UserValidator.isValid(user));
        user.setName("yandex!yandex.ru");
        assertFalse(UserValidator.isValid(user));
    }
    @Test
    void testBadUserLogin() {
        //логин не может быть пустым и содержать пробелы
        user.setLogin(null);
        assertFalse(UserValidator.isValid(user));
        user.setLogin("");
        assertFalse(UserValidator.isValid(user));
        user.setLogin("      ");
        assertFalse(UserValidator.isValid(user));
        user.setLogin("login ");
        assertFalse(UserValidator.isValid(user));
        user.setLogin(" login");
        assertFalse(UserValidator.isValid(user));
        user.setLogin(" login ");
        assertFalse(UserValidator.isValid(user));
        user.setLogin("lo gin");
        assertFalse(UserValidator.isValid(user));
    }

    @Test
    void testBadUserName() {
        //имя для отображения может быть пустым — в таком случае будет использован логин
        user.setName("");
        assertTrue(UserValidator.isValid(user));
        assertEquals(user.getName(),user.getLogin());
        user.setName(" ");
        assertTrue(UserValidator.isValid(user));
        assertEquals(user.getName(),user.getLogin());
        user.setName(null);
        assertTrue(UserValidator.isValid(user));
        assertEquals(user.getName(),user.getLogin());
    }

    @Test
    void testBadUserBirthday() {
        //дата рождения не может быть в будущем.
        user.setBirthday(LocalDate.now().plusDays(1));
        assertFalse(UserValidator.isValid(user));

    }
}
