package ru.yandex.practicum.filmorate.storage;

public interface FilmGenresStorage {
    void deleteAllGenres(Long id);

    void addGenreToFilm(long filmId, int genreId);
}
