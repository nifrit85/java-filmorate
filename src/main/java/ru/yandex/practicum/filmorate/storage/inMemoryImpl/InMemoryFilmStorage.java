package ru.yandex.practicum.filmorate.storage.inMemoryImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    Map<Long, Film> films = new HashMap<>();
    private long id = 1;

    @Override
    public Film create(Film film) {
        film.setId(id);
        id++;
        films.put(film.getId(), film);
        log.info("Добавлен фильм :" + film);
        return film;
    }

    @Override
    public Film update(Film film) {
        log.info("Фильм :" + films.get(film.getId()) + " заменён на " + film);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Film getFilmById(long id) {
        return films.get(id);
    }

    @Override
    public Collection<Film> getMostPopularFilms(int count) {
        return films
                .values()
                .stream()
                .sorted(Collections.reverseOrder(Comparator.comparingLong(film -> film.getLikes().size())))
                .limit(count)
                .collect(Collectors.toList());
    }
}
