package ru.yandex.practicum.filmorate.service.validator;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

@Slf4j
public class UserValidator {
    public static void isValid(User user) {
        if (user == null) {
            log.error("Ошибка валидации пользователя : Нет данных");
            throw new ValidationException("Заполните данные о пользователе");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}