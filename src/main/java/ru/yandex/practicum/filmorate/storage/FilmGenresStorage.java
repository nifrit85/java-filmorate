package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Set;

public interface FilmGenresStorage {
//    void updateGenresInFilm(Film film);
    void deleteAllGenres(Long id);
    void addGenreToFilm(long filmId, int genreId);
    Set<Genre> getGenresByFilmId(long id);
}
