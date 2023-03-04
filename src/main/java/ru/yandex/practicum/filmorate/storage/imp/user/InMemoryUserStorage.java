package ru.yandex.practicum.filmorate.storage.imp.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Component
@Qualifier("InMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {
    Map<Long, User> users = new HashMap<>();
    private long id = 1;

    @Override
    public User create(User user) {
        user.setId(id);
        id++;
        users.put(user.getId(), user);
        log.info("Добавлен пользователь :" + user);
        return user;
    }

    @Override
    public User update(User user) {
        log.info("Пользователь :" + users.get(user.getId()) + " заменён на " + user);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserById(long id) {
        return users.get(id);
    }

    @Override
    public void addFriend(long id, long friendId) {
        getUserById(id).addFriend(getUserById(friendId).getId());
        getUserById(friendId).addFriend(getUserById(id).getId());
    }

    @Override
    public List<User> getFriends(long id) {
        return getUserById(id)
                .getFriends()
                .stream()
                .map(this::getUserById)
                .collect(Collectors.toList());

    }

    @Override
    public List<User> getCommonFriends(long id, long friendId) {
        Set<Long> userFriendsIds = getUserById(id).getFriends();
        Set<Long> friendFriendsIds = getUserById(friendId).getFriends();
        Set<Long> commonFriends = new HashSet<>(userFriendsIds);
        commonFriends.retainAll(friendFriendsIds);
        return commonFriends.stream().map(this::getUserById).collect(Collectors.toList());
    }

    @Override
    public void deleteFriend(long id, long friendId) {
        getUserById(id).deleteFriend(friendId);
        getUserById(friendId).deleteFriend(id);
    }
}
