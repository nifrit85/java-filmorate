package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class Film {
    private final Set<Long> likes = new HashSet<>();
    long id;
    @NotBlank(message = "Название не может быть пустым") String name;
    @Size(max = 200, message = "Максимальная длина описания — 200 символов") String description;
    LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть положительной") Long duration;

    public void addLike(long id) {
        this.likes.add(id);
    }

    public void deleteLike(long id) {
        this.likes.remove(id);
    }
}
