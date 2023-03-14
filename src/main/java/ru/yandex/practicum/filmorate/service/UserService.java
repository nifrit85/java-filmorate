package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.validator.UserValidator;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;


@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("UserDb") UserStorage userStorage) {

        this.userStorage = userStorage;
    }

    public void addFriend(long id, long friendId) {
        getUserById(id);
        getUserById(friendId);
        userStorage.addFriend(id, friendId);
    }

    public void deleteFriend(long id, long friendId) {
        getUserById(id);
        getUserById(friendId);
        userStorage.deleteFriend(id, friendId);
    }

    public List<User> getFriends(long id) {
        getUserById(id);
        return userStorage.getFriends(id);
    }

    public List<User> getCommonFriends(long id, long friendId) {
        getUserById(id);
        getUserById(friendId);
        return userStorage.getCommonFriends(id, friendId);
    }

    public User create(User user) {
        UserValidator.isValid(user);
        return userStorage.create(user);
    }

    public User update(User user) {
        UserValidator.isValid(user);
        getUserById(user.getId());
        return userStorage.update(user);
    }

    public List<User> findAll() {
        return userStorage.findAll();
    }

    public User getUserById(long id) {
        User user = userStorage.getUserById(id);
        if (user == null) {
            throw new NotFoundException("Пользователь", id);
        }
        return user;
    }
}
