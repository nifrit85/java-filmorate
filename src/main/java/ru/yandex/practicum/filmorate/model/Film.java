package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@Builder
public class Film {
    long id;
    @NotBlank(message = "Название не может быть пустым")
    String name;
    @Size(max = 200)
    String description;
    LocalDate releaseDate;
    @Positive
    Long duration;
}
