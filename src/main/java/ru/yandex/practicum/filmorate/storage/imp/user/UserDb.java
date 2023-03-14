package ru.yandex.practicum.filmorate.storage.imp.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.query.QueryForUsers;

import java.util.ArrayList;
import java.util.List;

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
        return getUserById(userId);
    }

    @Override
    public User update(User user) {
        updateUser(user);
        return getUserById(user.getId());
    }

    @Override
    public List<User> findAll() {
        List<User> userList = new ArrayList<>();
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(QueryForUsers.SELECT_ALL);
        while (rowSet.next()) {
            User user = userBuilder(rowSet);
            if (user != null) {
                userList.add(user);
            }else {
                log.debug("Ошибка создания пользователя : " + rowSet);

            }
        }
        return userList;
    }

    @Override
    public User getUserById(long id) {
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(QueryForUsers.SELECT_BY_ID, id);
        if (rowSet.next()) {
            return userBuilder(rowSet);
        }
        log.debug("Ошибка получения пользователя с ID: " + id);
        return null;
    }

    private long createUser(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("USERS").usingGeneratedKeyColumns("USER_ID");
        long userId = simpleJdbcInsert.executeAndReturnKey(user.toMap()).longValue();
        log.debug("Пользователь с ID: " + userId + " успешно добавлен в БД");
        return userId;
    }

    private void updateUser(User user) {
        jdbcTemplate.update(QueryForUsers.UPDATE, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        log.debug("Пользователь с ID: " + user.getId() + " успешно обновлён в БД");
    }

    private User userBuilder(SqlRowSet rowSet) {
        User user = User.builder().id(rowSet.getLong("USER_ID")).email(rowSet.getString("EMAIL")).login(rowSet.getString("LOGIN")).name(rowSet.getString("NAME")).build();

        if (rowSet.getDate("BIRTHDAY") != null && user != null) {
            user.setBirthday(rowSet.getDate("BIRTHDAY").toLocalDate());
        } else if (rowSet.getDate("BIRTHDAY") == null) {
            log.debug("Ошибка получения даты рождения для пользователя с ID: " + rowSet.getLong("USER_ID"));
        }
        return user;
    }

    @Override
    public void addFriend(long id, long friendId) {
        Friendship friendship = getFriendship(id, friendId);
        if (friendship == null) { //Дружбы не существет
            createFriendship(id, friendId);
        } else { //Проверим что друг хочет подтвердить дружбу
            if (friendId == friendship.getUserId() && !friendship.getIsConfirmed()) {
                friendship.setIsConfirmed(true);
                updateFriendship(friendship);
            }
        }
    }

    @Override
    public void deleteFriend(long id, long friendId) {
        Friendship friendship = getFriendship(id, friendId);
        if (friendship != null) { //Дружба существет
            if (!friendship.getIsConfirmed() && friendship.getUserId() == id) //Не подтверждённая и у даляет тот кто запрашивал
                deleteFriendship(friendship.getFriendshipId());
            else if (friendship.getIsConfirmed() && friendship.getUserId() == id) { //Подтверждённая, но удаляет тот кто инициировал
                deleteFriendship(friendship.getFriendshipId()); // удаляем существующую дружбу
                createFriendship(friendId, id); //Создаём новую связь, но с другим порядком
            } else if (friendship.getIsConfirmed() && friendship.getUserId() == friendId) { //Подтверждённая, но удаляет тот кто подтверждал
                friendship.setIsConfirmed(false);
                updateFriendship(friendship);
            }
        }
    }

    @Override
    public List<User> getFriends(long id) {
        List<User> userList = new ArrayList<>();
        String sqlQuery = QueryForUsers.SELECT_ALL
                        + "where "
                        + QueryForUsers.USER_ID_IN
                        + QueryForUsers.SELECT_FRIEND;
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlQuery, id, id);
        while (rowSet.next()) {
            User user = userBuilder(rowSet);
            if (user != null) {
                userList.add(user);
            } else {
                log.debug("Ошибка создания пользователя :" + rowSet);
            }
        }
        return userList;
    }

    @Override
    public List<User> getCommonFriends(long id, long friendId) {
        List<User> userList = new ArrayList<>();

        String sqlQuery = QueryForUsers.SELECT_ALL
                        + "where "
                        + QueryForUsers.USER_ID_IN
                        + QueryForUsers.SELECT_FRIEND
                        + "and "
                        + QueryForUsers.USER_ID_IN
                        + QueryForUsers.SELECT_FRIEND;

        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlQuery, id, id, friendId, friendId);
        while (rowSet.next()) {
            User user = userBuilder(rowSet);
            if (user != null) {
                userList.add(user);
            } else {
                log.debug("Ошибка создания пользователя :" + rowSet);
            }
        }
        return userList;
    }

    private Friendship getFriendship(long id, long friendId) {
        String sqlQuery = QueryForUsers.SELECT_FRIENDSHIP
                        + "union "
                        + QueryForUsers.SELECT_FRIENDSHIP;

        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlQuery, id, friendId, friendId, id);
        if (rowSet.next()) {
            log.debug("Найдена дружба между пользователями " + id + " и " + friendId);
            return Friendship.builder().friendshipId(rowSet.getLong("FRIENDSHIP_ID")).userId(rowSet.getLong("USER_ID")).friendId(rowSet.getLong("FRIEND_ID")).isConfirmed(rowSet.getBoolean("IS_CONFIRMED")).build();

        } else {
            log.debug("Не найдена дружба между пользователями " + id + " и " + friendId);
            return null;
        }
    }

    private void createFriendship(long id, long friendId) {
        jdbcTemplate.update(QueryForUsers.INSERT_FRIENDSHIP, id, friendId);
        log.debug("Дружба между пользователями " + id + " и " + friendId + " создана");
    }

    private void updateFriendship(Friendship friendship) {
        jdbcTemplate.update(QueryForUsers.UPDATE_FRIENDSHIP, friendship.getUserId(), friendship.getFriendId(), friendship.getIsConfirmed(), friendship.getFriendshipId());
        log.debug("Дружба между пользователями " + friendship.getUserId() + " и " + friendship.getFriendId() + " обновлена");
    }

    private void deleteFriendship(Long id) {
        jdbcTemplate.update(QueryForUsers.DELETE_FRIENDSHIP, id);
        log.debug("Дружба с ID: " + id + " удалена");
    }
}


