package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.validator.UserValidator;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private final UserStorage storage;

    @Autowired
    public UserService(@Qualifier("UserDb") UserStorage storage) {
        this.storage = storage;
    }

    public void addFriend(long id, long friendId) {
        this.storage.addFriend(id, friendId);
    }

    public void deleteFriend(long id, long friendId) {
        this.storage.deleteFriend(id, friendId);
    }

    public Collection<User> getFriends(long id) {
        return this.storage.getFriends(id);
    }

    public Collection<User> getCommonFriends(long id, long friendId) {
        return this.storage.getCommonFriends(id, friendId);
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
