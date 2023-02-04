package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class Film {
    long id;
    @NotBlank(message = "Название не может быть пустым") String name;
    @Size(max = 200, message = "Максимальная длина описания — 200 символов") String description;
    LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть положительной") Long duration;

    Set<Long> likes;
}
