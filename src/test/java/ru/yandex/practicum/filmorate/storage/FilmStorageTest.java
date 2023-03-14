package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.imp.film.FilmDb;
import ru.yandex.practicum.filmorate.storage.imp.mpa.MpaDb;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FilmStorageTest {
    private final FilmDb storage;
    private final MpaDb mpaStorage;

    @Test
    void testCreate() {
        Film film = Film.builder()
                .name("New Film")
                .description("New Film Descr")
                .releaseDate(LocalDate.now())
                .duration(200L)
                .mpa(mpaStorage.getMpaById(1))
                .build();
        //Записали в БД
        storage.create(film);
        //Сверили что в БД записало точно то, что положили
        assertEquals(film, storage.getFilmById(film.getId()));
    }

    @Test
    void testUpdate() {
        Film film = storage.getFilmById(3);
        film.setName("Updated Name");
        film.setDescription("Updated Descr");
        film.setDuration(100L);
        film.setMpa(mpaStorage.getMpaById(5));
        //Записали в БД
        storage.update(film);
        //Сверили что в БД записало точно то, что положили
        assertEquals(film, storage.getFilmById(film.getId()));
    }

    @Test
    void testFindAll() {
        Collection<Film> films = storage.findAll();
        // БД 3 фильма
        assertEquals(3, films.size());
        assertTrue(films.contains(storage.getFilmById(1)));
        assertTrue(films.contains(storage.getFilmById(2)));
        assertTrue(films.contains(storage.getFilmById(3)));
    }

    @Test
    void testGetMostPopularFilms() {
        List<Film> films = storage.getMostPopularFilms(10);
        // БД 3 фильма
        assertEquals(3, films.size());
        //Порядок 3,1,2
        //Количество лайков 3,2,1
        assertEquals(storage.getFilmById(3), films.get(0));
        assertEquals(3, films.get(0).getLikes().size());
        assertEquals(storage.getFilmById(1), films.get(1));
        assertEquals(2, films.get(1).getLikes().size());
        assertEquals(storage.getFilmById(2), films.get(2));
        assertEquals(1, films.get(2).getLikes().size());

        films = storage.getMostPopularFilms(1);
        //Топ 1 фильм это третий
        assertEquals(1, films.size());
        assertEquals(storage.getFilmById(3), films.get(0));
    }

    @Test
    void testAddLike() {
        Film film = storage.getFilmById(1);
        //Проверяем текущее состояние
        assertEquals(2, film.getLikes().size());

        //Ставим лайк
        storage.addLike(film.getId(), 3L);
        film = storage.getFilmById(1);
        assertEquals(3, film.getLikes().size());

        //Повторный лайк не должен защитаться
        storage.addLike(film.getId(), 2L);
        film = storage.getFilmById(1);
        assertEquals(3, film.getLikes().size());
    }

    @Test
    void testRemoveLike() {
        Film film = storage.getFilmById(1);
        //Проверяем текущее состояние
        assertEquals(2, film.getLikes().size());

        //Удаляем лайк
        storage.removeLike(film.getId(), 2L);
        film = storage.getFilmById(1);
        assertEquals(1, film.getLikes().size());

        //Неверный идентификатор
        storage.removeLike(film.getId(), 2L);
        film = storage.getFilmById(1);
        assertEquals(1, film.getLikes().size());

        //Фильм без лайков
        storage.removeLike(film.getId(), 1L);
        film = storage.getFilmById(1);
        assertTrue(film.getLikes().isEmpty());

        //Фильм без лайков. Удаляем лайк
        storage.removeLike(film.getId(), 1L);
        film = storage.getFilmById(1);
        assertTrue(film.getLikes().isEmpty());
    }
}
