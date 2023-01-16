package ru.yandex.practicum.filmorate.controller.validator;


import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;


public class UserValidator {
    public static boolean isValid(User user) {
        if ((user.getEmail().isBlank() || !user.getEmail().contains("@") || user.getLogin().isBlank() || user.getLogin().contains(" ") || user.getBirthday().isAfter(LocalDate.now())))
            return false;
        return true;
    }
}
