package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.validator.UserValidator;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private final UserStorage storage;

    @Autowired
    public UserService(UserStorage storage) {
        this.storage = storage;
    }

    public void addFriend(long id, long friendId) {
        getUserById(id).addFriend(getUserById(friendId).getId());
        getUserById(friendId).addFriend(getUserById(id).getId());
    }

    public void deleteFriend(long id, long friendId) {
        getUserById(id).deleteFriend(friendId);
        getUserById(friendId).deleteFriend(id);
    }

    public Collection<User> getFriends(long id) {
        return getUserById(id).getFriends().stream().map(storage::getUserById).collect(Collectors.toList());
    }

    public Collection<User> getCommonFriends(long id, long friendId) {
        Set<Long> userFriendsIds = getUserById(id).getFriends();
        Set<Long> friendFriendsIds = getUserById(friendId).getFriends();
        Set<Long> commonFriends = new HashSet<>(userFriendsIds);
        commonFriends.retainAll(friendFriendsIds);
        return commonFriends.stream().map(storage::getUserById).collect(Collectors.toList());
    }

    public User create(User user) {
        UserValidator.isValid(user);
        return storage.create(user);
    }

    public User update(User user) {
        UserValidator.isValid(user);
        getUserById(user.getId()); //Проверка на существование. Выдаёт исключение
        return storage.update(user);
    }

    public Collection<User> findAll() {
        return storage.findAll();
    }

    public User getUserById(long id) {
        User user = storage.getUserById(id);
        if (user == null) {
            throw new NotFoundException("Пользователь", id);
        }
        return user;
    }
}
