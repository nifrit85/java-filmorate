package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.imp.film.FilmDb;
import ru.yandex.practicum.filmorate.storage.imp.filmgenre.FilmGenresDb;
import ru.yandex.practicum.filmorate.storage.imp.genre.GenreDb;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FilmGenresStorageTest {

    private final FilmGenresDb storage;

    private final FilmDb filmStorage;
    private final GenreDb genreStorage;

    @Test
    void testDeleteAllGenres() {
        //У третьего фильма, 3 жанра
        Set<Genre> genreList = filmStorage.getFilmById(3L).getGenres();
        assertEquals(3, genreList.size());
        storage.deleteAllGenres(3L);
        genreList = filmStorage.getFilmById(3L).getGenres();
        assertTrue(genreList.isEmpty());
    }

    @Test
    void testAddGenreToFilm() {
        Set<Genre> genreList = filmStorage.getFilmById(1L).getGenres();
        //Не содержит 3ий жарн
        assertFalse(genreList.contains(genreStorage.getGenreById(3)));
        //Добавим 3 жанр
        storage.addGenreToFilm(1L, 3);
        genreList = filmStorage.getFilmById(1L).getGenres();
        assertTrue(genreList.contains(genreStorage.getGenreById(3)));
    }
}
