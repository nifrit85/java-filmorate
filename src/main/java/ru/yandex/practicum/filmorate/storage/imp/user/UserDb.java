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
    private String sqlQuery;

    @Autowired
    public UserDb(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User create(User user) {
        long userId = createUser(user);
        user.setId(userId);
        User userToReturn = getUserById(userId);
        log.info("Добавлен пользователь :" + userToReturn);
        return userToReturn;
    }

    @Override
    public User update(User user) {
        log.info("Пользователь :" + getUserById(user.getId()) + " заменён на " + user);
        updateUser(user);
        return getUserById(user.getId());
    }

    @Override
    public List<User> findAll() {
        List<User> userList = new ArrayList<>();

        sqlQuery = QueryForUsers.SELECT_ALL;
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlQuery);
        while (rowSet.next()) {
            User user = userBuilder(rowSet);
            if (user != null) {
                userList.add(user);
            }
        }
        return userList;
    }

    @Override
    public User getUserById(long id) {
        sqlQuery = QueryForUsers.SELECT_BY_ID;
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (rowSet.next()) {
            return userBuilder(rowSet);
        }
        return null;
    }

    private long createUser(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("USERS")
                .usingGeneratedKeyColumns("USER_ID");
        return simpleJdbcInsert.executeAndReturnKey(user.toMap()).longValue();
    }

    private void updateUser(User user) {
        sqlQuery = QueryForUsers.UPDATE;
        jdbcTemplate.update(sqlQuery, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
    }

    private User userBuilder(SqlRowSet rowSet) {
        User user;
        try {
            user = User.builder()
                    .id(rowSet.getLong("USER_ID"))
                    .email(rowSet.getString("EMAIL"))
                    .login(rowSet.getString("LOGIN"))
                    .name(rowSet.getString("NAME"))
                    .birthday(rowSet.getDate("BIRTHDAY").toLocalDate())
                    .build();

        } catch (NullPointerException e) {
            user = null;
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
        sqlQuery = QueryForUsers.SELECT_ALL
                + "where "
                + QueryForUsers.USER_ID_IN
                + QueryForUsers.SELECT_FRIEND;
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlQuery, id, id);
        while (rowSet.next()) {
            User user = userBuilder(rowSet);
            if (user != null) {
                userList.add(user);
            }
        }
        return userList;
    }

    @Override
    public List<User> getCommonFriends(long id, long friendId) {
        List<User> userList = new ArrayList<>();

        sqlQuery = QueryForUsers.SELECT_ALL
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
            }
        }
        return userList;
    }

    private Friendship getFriendship(long id, long friendId) {
        sqlQuery = QueryForUsers.SELECT_FRIENDSHIP
                + "union "
                + QueryForUsers.SELECT_FRIENDSHIP;

        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlQuery, id, friendId, friendId, id);
        if (rowSet.next()) {
            return Friendship.builder().friendshipId(rowSet.getLong("FRIENDSHIP_ID")).userId(rowSet.getLong("USER_ID")).friendId(rowSet.getLong("FRIEND_ID")).isConfirmed(rowSet.getBoolean("IS_CONFIRMED")).build();
        } else {
            return null;
        }
    }

    private void createFriendship(long id, long friendId) {
        sqlQuery = QueryForUsers.INSERT_FRIENDSHIP;
        jdbcTemplate.update(sqlQuery, id, friendId);
    }

    private void updateFriendship(Friendship friendship) {
        sqlQuery = QueryForUsers.UPDATE_FRIENDSHIP;
        jdbcTemplate.update(sqlQuery, friendship.getUserId(), friendship.getFriendId(), friendship.getIsConfirmed(), friendship.getFriendshipId());
    }

    private void deleteFriendship(Long id) {
        sqlQuery = QueryForUsers.DELETE_FRIENDSHIP;
        jdbcTemplate.update(sqlQuery, id);
    }
}


