package ru.yandex.practicum.filmorate.controller.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserValidatorTest {
    private User user;
    private Set<ConstraintViolation<User>> violations;
    private Validator validator;

    @BeforeEach
    void beforeEach() {
        user = User.builder().email("yandex@yandex.ru").login("yandex").name("Аркадий Волож").birthday(LocalDate.of(1964, 2, 11)).build();

        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.usingContext().getValidator();
    }

    @Test
    void testGoodUser() {
        assertDoesNotThrow(() -> UserValidator.isValid(user));
    }

    @Test
    void testGoodBirthday() {
        //С днём рождения!
        user.setBirthday(LocalDate.now());
        assertDoesNotThrow(() -> UserValidator.isValid(user));
    }

    @Test
    void testBadName() {
        //имя для отображения может быть пустым — в таком случае будет использован логин
        user.setName("");
        assertDoesNotThrow(() -> UserValidator.isValid(user));
        assertEquals(user.getName(), user.getLogin());
        user.setName(" ");
        assertDoesNotThrow(() -> UserValidator.isValid(user));
        assertEquals(user.getName(), user.getLogin());
        user.setName(null);
        assertDoesNotThrow(() -> UserValidator.isValid(user));
        assertEquals(user.getName(), user.getLogin());
    }

    @Test
    void testBadEmail() {
        //электронная почта не может быть пустой и должна содержать символ @
        user.setEmail(null);
        violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        violations.stream().map(ConstraintViolation::getMessage).forEach(System.out::println);

        user.setEmail("");
        violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        violations.stream().map(ConstraintViolation::getMessage).forEach(System.out::println);

        user.setEmail("  ");
        violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        violations.stream().map(ConstraintViolation::getMessage).forEach(System.out::println);

        user.setName("yandex!yandex.ru");
        violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        violations.stream().map(ConstraintViolation::getMessage).forEach(System.out::println);

        user.setName("yandex@y.a.n.d.e.x.ru");
        violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        violations.stream().map(ConstraintViolation::getMessage).forEach(System.out::println);

        user.setName("@.ru");
        violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        violations.stream().map(ConstraintViolation::getMessage).forEach(System.out::println);
    }

    @Test
    void testBadLogin() {
        //логин не может быть пустым и содержать пробелы
        user.setLogin(null);
        violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        violations.stream().map(ConstraintViolation::getMessage).forEach(System.out::println);

        user.setLogin("");
        violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        violations.stream().map(ConstraintViolation::getMessage).forEach(System.out::println);

        user.setLogin("      ");
        violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        violations.stream().map(ConstraintViolation::getMessage).forEach(System.out::println);

        user.setLogin("login ");
        violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        violations.stream().map(ConstraintViolation::getMessage).forEach(System.out::println);

        user.setLogin(" login");
        violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        violations.stream().map(ConstraintViolation::getMessage).forEach(System.out::println);

        user.setLogin(" login ");
        violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        violations.stream().map(ConstraintViolation::getMessage).forEach(System.out::println);

        user.setLogin("lo gin");
        violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        violations.stream().map(ConstraintViolation::getMessage).forEach(System.out::println);
    }

    @Test
    void testBadBirthday() {
        //дата рождения не может быть в будущем.
        user.setBirthday(LocalDate.now().plusDays(1));
        violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        violations.stream().map(ConstraintViolation::getMessage).forEach(System.out::println);
    }
}
