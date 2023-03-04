package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;


public interface FilmStorage {
    Film create(Film film);

    Film update(Film film);

    List<Film> findAll();

    Film getFilmById(long id);

    List<Film> getMostPopularFilms(int count);

    void addLike(Long id, Long userId);

    void removeLike(Long id, Long userId);
}
