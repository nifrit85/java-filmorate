package ru.yandex.practicum.filmorate.controller.validator;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

public class UserValidator {
    public static void isValid(User user) throws ValidationException {
        if (user.getName() == null || user.getName().isBlank())
            user.setName(user.getLogin());

        if (user.getLogin().contains(" "))
            throw new ValidationException("Логин не может содержать пробелы");
    }
}