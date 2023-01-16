package ru.yandex.practicum.filmorate.controller.validator;


import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;


public class UserValidator {
    public static boolean isValid(User user) {
        if (user.getLogin() != null && !user.getLogin().isBlank() && !user.getLogin().contains(" ")) {
            if (user.getName() == null || user.getName().isBlank()) user.setName(user.getLogin());
        }
        if ( user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@") ||
                user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ") ||
                user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now()))
            return false;
        return true;
    }
}
