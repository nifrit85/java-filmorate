package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@Builder
public class User {
    long id;
    @Email(message = "Не корректный адрес электронной почты")
    @NotBlank(message = "Адрес электронной почты не может быть пустым")
    String email;
    @NotBlank(message = "Логин не может быть пустым")
    @Pattern(regexp = "\\S+",message = "Логин не может содержать пробелы")
    String login;
    String name;
    @Past(message = "Дата рождения не может быть в будущем")
    LocalDate birthday;
}
