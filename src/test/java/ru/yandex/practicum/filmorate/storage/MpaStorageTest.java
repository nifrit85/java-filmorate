package ru.yandex.practicum.filmorate.storage;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.imp.mpa.MpaDb;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class MpaStorageTest {
    private final MpaDb storage;

    @Test
    void testGetMpaById() {
        Mpa mpa = storage.getMpaById(1);
        assertEquals("G", mpa.getName());
        mpa = storage.getMpaById(4);
        assertEquals("R", mpa.getName());
    }

    @Test
    void testFindAll() {
        List<Mpa> mpaList = storage.findAll();
        assertEquals(5, mpaList.size());
        assertTrue(mpaList.contains(storage.getMpaById(1)));
        assertTrue(mpaList.contains(storage.getMpaById(2)));
        assertTrue(mpaList.contains(storage.getMpaById(3)));
        assertTrue(mpaList.contains(storage.getMpaById(4)));
        assertTrue(mpaList.contains(storage.getMpaById(5)));
    }
}
