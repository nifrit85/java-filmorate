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

import java.util.ArrayList;
import java.util.Collection;
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
        log.info("Добавлен пользователь :" + user);
        return user;
    }

    @Override
    public User update(User user) {

        log.info("Пользователь :" + getUserById(user.getId()) + " заменён на " + user);
        updateUser(user);
        return user;
    }

    @Override
    public Collection<User> findAll() {
        List<User> userList = new ArrayList<>();

        sqlQuery = "select * from USERS";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlQuery);
        if (rowSet.next()) {
            User user = userBuilder(rowSet);
            if (user != null) {
                userList.add(user);
            }
        }
        return userList;
    }

    @Override
    public User getUserById(long id) {
        User user = null;
        sqlQuery = "select * from USERS where USER_ID = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (rowSet.next()) {
            user = userBuilder(rowSet);
        }
        return user;
    }

    private long createUser(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("USERS").usingGeneratedKeyColumns("USER_ID");
        return simpleJdbcInsert.executeAndReturnKey(user.toMap()).longValue();
    }

    private void updateUser(User user) {
        sqlQuery = "update USERS set EMAIL = ?, LOGIN = ?, NAME = ?, BIRTHDAY = ? WHERE USER_ID = ?";
        jdbcTemplate.update(sqlQuery, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
    }

    private User userBuilder(SqlRowSet rowSet) {
        User user;
        try {
            user = User.builder().id(rowSet.getLong("USER_ID")).email(rowSet.getString("EMAIL")).login(rowSet.getString("LOGIN")).name(rowSet.getString("NAME")).birthday(rowSet.getDate("BIRTHDAY").toLocalDate()).build();

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
            if (friendId == friendship.getUserId() && friendship.getIsConfirmed() == false) {
                friendship.setIsConfirmed(true);
                updateFriendship(friendship);
            }
        }
    }

    @Override
    public void deleteFriend(long id, long friendId) {
        Friendship friendship = getFriendship(id, friendId);
        if (friendship != null) { //Дружба существет
            if (friendship.getIsConfirmed() == false && friendship.getUserId() == id) //Не подтверждённая и у даляет тот кто запрашивал
                deleteFriendship(friendship.getFriendshipId());
            else if (friendship.getIsConfirmed() == true && friendship.getUserId() == id) { //Подтверждённая, но удаляет тот кто инициировал
                deleteFriendship(friendship.getFriendshipId()); // удаляем существующую дружбу
                createFriendship(friendId, id); //Создаём новую связь, но с другим порядком
            } else if (friendship.getIsConfirmed() == true && friendship.getUserId() == friendId) { //Подтверждённая, но удаляет тот кто подтверждал
                friendship.setIsConfirmed(false);
                updateFriendship(friendship);
            }
        }
    }

    @Override
    public Collection<User> getFriends(long id) {
        List<User> userList = new ArrayList<>();
        sqlQuery = "select * from USERS where USER_ID in " +
                "( select FRIEND_ID from FRIENDSHIP where USER_ID = ?)" +
                "UNION (select USER_ID from FRIENDSHIP where FRIEND_ID = ? and IS_CONFIRMED = true)";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlQuery, id, id);
        if (rowSet.next()) {
            User user = userBuilder(rowSet);
            if (user != null) {
                userList.add(user);
            }
        }
        return userList;
    }

    @Override
    public Collection<User> getCommonFriends(long id, long friendId) {
        return null;
    }

    private Friendship getFriendship(long id, long friendId) {
        Friendship friendship;
        sqlQuery = "select * from FRIENDSHIP where USER_ID = ? and FRIEND_ID = ?" + "union " + "select * from FRIENDSHIP where USER_ID = ? and FRIEND_ID = ?";

        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlQuery, id, friendId, friendId, id);
        if (rowSet.next()) {
            return friendship = Friendship.builder().friendshipId(rowSet.getLong("FRIENDSHIP_ID")).userId(rowSet.getLong("USER_ID")).friendId(rowSet.getLong("FRIEND_ID")).isConfirmed(rowSet.getBoolean("IS_CONFIRMED")).build();
        } else {
            return null;
        }
    }

    private void createFriendship(long id, long friendId) {
        sqlQuery = "insert into FRIENDSHIP (USER_ID,FRIEND_ID,IS_CONFIRMED) values (?,?,false)";
        jdbcTemplate.update(sqlQuery, id, friendId);
    }

    private void updateFriendship(Friendship friendship) {
        sqlQuery = "update FRIENDSHIP set USER_ID = ?, FRIEND_ID = ?, IS_CONFIRMED = ? where FRIENDSHIP_ID = ?";
        jdbcTemplate.update(sqlQuery, friendship.getUserId(), friendship.getFriendId(), friendship.getIsConfirmed(), friendship.getFriendshipId());
    }

    private void deleteFriendship(Long id) {
        sqlQuery = "delete from FRIENDSHIP where FRIENDSHIP_ID = ?";
        jdbcTemplate.update(sqlQuery, id);
    }


}


