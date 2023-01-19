package ru.yandex.practicum.filmorate.controller.validator;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

public class UserValidator {
    public static void isValid(User user) throws ValidationException {
        if (user == null) throw new ValidationException("Заполните данные о пользователе");
        if (user.getName() == null || user.getName().isBlank()) user.setName(user.getLogin());
    }
}