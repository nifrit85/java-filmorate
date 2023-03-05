package ru.yandex.practicum.filmorate.storage;

import java.util.List;

public interface FilmGenresStorage {
    void deleteAllGenres(Long id);

    void addGenreToFilm(long filmId, int genreId);

    void addGenresToFilm(long filmId, List<Integer> genresId);
}
