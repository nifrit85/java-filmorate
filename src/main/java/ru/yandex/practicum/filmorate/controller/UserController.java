package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.controller.validator.UserValidator;
import ru.yandex.practicum.filmorate.model.User;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
public class UserController {
    private int id = 1;
    Map<Integer, User> users = new HashMap<>();

    @PostMapping(value = "/users")
    public User create(@RequestBody User user){
        if (UserValidator.isValid(user)) {
            user.setId(id);
            id++;
            users.put(user.getId(), user);
            log.info("Добавлен пользователь :" + user);
            return user;
        }else {
                log.error("Ошибка валидации пользователя :" + user);
         throw new ValidationException("Некорректные данные пользователя");}
    }

    @PutMapping(value = "/users")
    public User update(@RequestBody User user){
        if (UserValidator.isValid(user)) {
            if (users.containsKey(user.getId())) {
                log.info("Пользователь :" + users.get(user.getId()) + " заменён на " + user);
                users.put(user.getId(), user);
                return user;
            }else {
                log.error("Не найден пользователь :" + user);
                throw new RuntimeException("Не найден пользователь");
            }
        }else {
            log.error("Ошибка валидации пользователя :" + user);
            throw new ValidationException("Некорректные данные пользователя");}
    }

    @GetMapping("/users")
    public Collection<User> findAll() {
        return users.values();
    }
}
