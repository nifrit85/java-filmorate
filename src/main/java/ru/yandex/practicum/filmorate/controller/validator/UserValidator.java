package ru.yandex.practicum.filmorate.controller.validator;

import ru.yandex.practicum.filmorate.model.User;

public class UserValidator {
    public static void isValid(User user){
        if (user.getName() == null || user.getName().isBlank())
            user.setName(user.getLogin());
    }
}