package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.validator.UserValidator;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage{
    private long id = 1;
    Map<Long, User> users = new HashMap<>();

    @Override
    public User create(User user) {
        UserValidator.isValid(user);

        user.setId(id);
        id++;
        users.put(user.getId(), user);
        log.info("Добавлен пользователь :" + user);
        return user;
    }

    @Override
    public User update(User user) {
        UserValidator.isValid(user);

        if (users.containsKey(user.getId())) {
            log.info("Пользователь :" + users.get(user.getId()) + " заменён на " + user);
            users.put(user.getId(), user);
            return user;
        } else {
            log.error("Не найден пользователь :" + user);
            throw new RuntimeException("Не найден пользователь");
        }
    }

    @Override
    public Collection<User> findAll() {
        return users.values();
    }
}
