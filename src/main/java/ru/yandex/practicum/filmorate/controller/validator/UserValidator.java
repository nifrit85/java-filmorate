package ru.yandex.practicum.filmorate.controller.validator;

import ru.yandex.practicum.filmorate.model.User;
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserValidator {
    public static boolean isValid(User user) {
        if (user == null ) return false;

        String login = user.getLogin();
        String name  = user.getName();
        String email = user.getEmail();

        LocalDate birthday = user.getBirthday();
        if (login != null && !login.isBlank() && !login.contains(" ") && (name == null || name.isBlank())) {
            user.setName(login);
        }
        return  email != null && !email.isBlank() && checkEmail(email) &&
                login != null && !login.isBlank() && !login.contains(" ") &&
                birthday != null && !birthday.isAfter(LocalDate.now());
    }
    private static boolean checkEmail(String email){
        String regex = "^(.+)@(.+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return  matcher.matches();
    }
}