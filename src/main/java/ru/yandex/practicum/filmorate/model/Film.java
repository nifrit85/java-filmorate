package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NonNull;

import java.time.Duration;
import java.time.LocalDate;
@Data
public class Film {
    int id;
    String name;
    String description;
    LocalDate releaseDate;
    Long duration;
}
