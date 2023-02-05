package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;


public interface FilmStorage {

    Film create(Film film);

    Film update(Film film);

    Collection<Film> findAll();

    Film getFilmById(long id);

    Collection<Film> getMostPopularFilms(int count);
}
