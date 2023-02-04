package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class User {
    private final Set<Long> friends = new HashSet<>();
    long id;
    @Email(message = "Не корректный адрес электронной почты") @NotBlank(message = "Адрес электронной почты не может быть пустым") String email;
    @NotBlank(message = "Логин не может быть пустым") @Pattern(regexp = "\\S+", message = "Логин не может содержать пробелы") String login;
    String name;
    @Past(message = "Дата рождения не может быть в будущем") LocalDate birthday;

    public void addFriend(long id) {
        this.friends.add(id);
    }

    public void deleteFriend(long id) {
        this.friends.remove(id);
    }
}
