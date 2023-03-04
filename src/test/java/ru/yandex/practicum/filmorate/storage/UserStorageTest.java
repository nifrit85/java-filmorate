package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.imp.user.UserDb;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserStorageTest {
    private final UserDb storage;

    @Test
    void testCreate() {
        User user = User.builder()
                .email("new@email.ru")
                .login("NewLogin")
                .name("NewName")
                .birthday(LocalDate.now().minusYears(25))
                .build();
        //Записали в БД
        storage.create(user);
        //Сверили что в БД записало точно то, что положили
        assertEquals(user, storage.getUserById(user.getId()));
    }

    @Test
    void testUpdate() {
        User user = storage.getUserById(3);
        user.setName("UpdatedName");
        user.setLogin("UpdatedLogin");
        user.setEmail("Updated@yandex.ru");
        user.setBirthday(LocalDate.now().minusYears(10));
        //Записали в БД
        storage.update(user);
        //Сверили что в БД записало точно то, что положили
        assertEquals(user, storage.getUserById(user.getId()));
    }

    @Test
    void testFindAll() {
        Collection<User> users = storage.findAll();
        // БД 5 пользователей
        assertEquals(5, users.size());
        assertTrue(users.contains(storage.getUserById(1)));
        assertTrue(users.contains(storage.getUserById(2)));
        assertTrue(users.contains(storage.getUserById(3)));
        assertTrue(users.contains(storage.getUserById(4)));
        assertTrue(users.contains(storage.getUserById(5)));
    }

    @Test
    void testAddFriends() {
        List<User> userList = storage.getFriends(1L);
        //Пользователь 1 дружит с 2 и 3
        assertEquals(2, userList.size());
        //Не дружит с 4
        assertFalse(userList.contains(storage.getUserById(4L)));
        //Добавляем дружбу 1-4
        storage.addFriend(1L, 4L);
        userList = storage.getFriends(1L);
        assertEquals(3, userList.size());
        assertTrue(userList.contains(storage.getUserById(4L)));

        // 1 дружит с 2, но 2 не дружит с 1
        assertTrue(userList.contains(storage.getUserById(2L)));
        userList = storage.getFriends(2L);
        assertFalse(userList.contains(storage.getUserById(1L)));

        //Сделаем дружбу двусторонней
        storage.addFriend(2L, 1L);
        userList = storage.getFriends(2L);
        assertTrue(userList.contains(storage.getUserById(1L)));
    }

    @Test
    void testDeleteFriends() {
        // 1 дружит с 3 и 3 с 1
        List<User> userList = storage.getFriends(1L);
        assertTrue(userList.contains(storage.getUserById(3L)));
        userList = storage.getFriends(3L);
        assertTrue(userList.contains(storage.getUserById(1L)));

        //3 больше не дружит с 1
        storage.deleteFriend(3L, 1L);
        userList = storage.getFriends(3L);
        assertFalse(userList.contains(storage.getUserById(1L)));
        //1 все еще дружит с 3
        userList = storage.getFriends(1L);
        assertTrue(userList.contains(storage.getUserById(3L)));

        //1 больше не дружит с 3
        storage.deleteFriend(1L, 3L);
        userList = storage.getFriends(1L);
        assertFalse(userList.contains(storage.getUserById(3L)));
        userList = storage.getFriends(3L);
        assertFalse(userList.contains(storage.getUserById(1L)));
    }

    @Test
    void testGetCommonFriends() {
        //1 дружит с 2 и 3, а 5 дружит с 3 и 4
        //Общий друг это 3
        List<User> userList = storage.getCommonFriends(1L, 5L);
        assertEquals(1, userList.size());
        assertTrue(userList.contains(storage.getUserById(3L)));
    }
}
