package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.imp.genre.GenreDb;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class GenreStorageTest {
    private final GenreDb storage;

    @Test
    void testGetGenreById() {
        Genre genre = storage.getGenreById(1);
        assertEquals("Комедия", genre.getName());
        genre = storage.getGenreById(4);
        assertEquals("Триллер", genre.getName());
    }

    @Test
    void testFindAll() {
        List<Genre> genreList = storage.findAll();
        assertEquals(6, genreList.size());
        assertTrue(genreList.contains(storage.getGenreById(1)));
        assertTrue(genreList.contains(storage.getGenreById(2)));
        assertTrue(genreList.contains(storage.getGenreById(3)));
        assertTrue(genreList.contains(storage.getGenreById(4)));
        assertTrue(genreList.contains(storage.getGenreById(5)));
        assertTrue(genreList.contains(storage.getGenreById(6)));
    }
}
