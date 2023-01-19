package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.controller.validator.UserValidator;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
public class UserController {
    private long id = 1;
    Map<Long, User> users = new HashMap<>();

    @PostMapping(value = "/users")
    public User create(@Valid @RequestBody User user) {
        try {
            UserValidator.isValid(user);
        } catch (ValidationException e) {
            log.error("Ошибка валидации пользователя :" + e.getMessage() + " Данные пользователя: " + user);
            throw e;
        }

        user.setId(id);
        id++;
        users.put(user.getId(), user);
        log.info("Добавлен пользователь :" + user);
        return user;
    }

    @PutMapping(value = "/users")
    public User update(@Valid @RequestBody User user) {
        try {
            UserValidator.isValid(user);
        } catch (ValidationException e) {
            log.error("Ошибка валидации пользователя :" + e.getMessage() + " Данные пользователя: " + user);
            throw e;
        }

        if (users.containsKey(user.getId())) {
            log.info("Пользователь :" + users.get(user.getId()) + " заменён на " + user);
            users.put(user.getId(), user);
            return user;
        } else {
            log.error("Не найден пользователь :" + user);
            throw new RuntimeException("Не найден пользователь");
        }
    }

    @GetMapping("/users")
    public Collection<User> findAll() {
        return users.values();
    }
}
