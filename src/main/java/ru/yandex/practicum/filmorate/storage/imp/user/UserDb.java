package ru.yandex.practicum.filmorate.storage.imp.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@Slf4j
@Component
@Qualifier("UserDb")
public class UserDb implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDb(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User create(User user) {
        long userId = createUser(user);
        user.setId(userId);
        log.info("Добавлен пользователь :" + user);
        return user;
    }

    @Override
    public User update(User user) {
        return null;
    }

    @Override
    public Collection<User> findAll() {
        return null;
    }

    @Override
    public User getUserById(long id) {
        return null;
    }
    //    @Override
//    public User update(User user) {
//        log.info("Пользователь :" + users.get(user.getId()) + " заменён на " + user);
//        users.put(user.getId(), user);
//        return user;
//    }
//
//    @Override
//    public Collection<User> findAll() {
//        return users.values();
//    }
//
//    @Override
//    public User getUserById(long id) {
//        return users.get(id);
//    }

    private long createUser(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("USERS").usingGeneratedKeyColumns("USER_ID");
        return simpleJdbcInsert.executeAndReturnKey(user.toMap()).longValue();
    }
}
