package ru.yandex.practicum.filmorate.controller.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserValidatorTest {
    private User user;

    @BeforeEach
    void beforeEach(){
        user = User.builder()
                .email("yandex@yandex.ru")
                .login("yandex")
                .name("Аркадий Волож")
                .birthday(LocalDate.of(1964,2,11))
                .build();
    }

    @Test
    void testGoodUser() {
        assertDoesNotThrow(()-> UserValidator.isValid(user));
    }

    @Test
    void testGoodBirthday() {
        //С днём рождения!
        user.setBirthday(LocalDate.now());
        assertDoesNotThrow(()-> UserValidator.isValid(user));
    }

    @Test
    void testBadLogin() {
        //логин не может содержать пробелы
        user.setLogin("      ");
        assertThrows(ValidationException.class,()->UserValidator.isValid(user));
        user.setLogin("login ");
        assertThrows(ValidationException.class,()->UserValidator.isValid(user));
        user.setLogin(" login");
        assertThrows(ValidationException.class,()->UserValidator.isValid(user));
        user.setLogin(" login ");
        assertThrows(ValidationException.class,()->UserValidator.isValid(user));
        user.setLogin("lo gin");
        assertThrows(ValidationException.class,()->UserValidator.isValid(user));
    }

    @Test
    void testBadName() {
        //имя для отображения может быть пустым — в таком случае будет использован логин
        user.setName("");
        assertDoesNotThrow(()-> UserValidator.isValid(user));
        assertEquals(user.getName(),user.getLogin());
        user.setName(" ");
        assertDoesNotThrow(()-> UserValidator.isValid(user));
        assertEquals(user.getName(),user.getLogin());
        user.setName(null);
        assertDoesNotThrow(()-> UserValidator.isValid(user));
        assertEquals(user.getName(),user.getLogin());
    }
}
