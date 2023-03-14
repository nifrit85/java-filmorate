package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        User userToReturn = userService.create(user);
        log.info("Добавлен пользователь :" + userToReturn.toString());
        return userToReturn;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        User userToReturn = userService.update(user);
        log.info("Пользователь :" + user.toString() + " заменён на " + userToReturn.toString());
        return userToReturn;
    }

    @GetMapping
    public List<User> findAll() {
        log.info("Возвращаем список пользователей");
        return userService.findAll();
    }

    @GetMapping("{id}")
    public User getUserById(@PathVariable long id) {
        log.info("Получаем пользователя с ID: " + id);
        return userService.getUserById(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable long id, @PathVariable long friendId) {
        log.info("Пользователь с ID: " + id + " запрашивает дружбу с пользователем с ID: " + friendId);
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable long id, @PathVariable long friendId) {
        log.info("Пользователь с ID: " + id + " отменяет дружбу с пользователем с ID: " + friendId);
        userService.deleteFriend(id, friendId);
    }

    @GetMapping("{id}/friends")
    public List<User> getFriends(@PathVariable long id) {
        log.info("Возвращаем список друзей пользователя с ID: " + id);
        return userService.getFriends(id);
    }

    @GetMapping("{id}/friends/common/{otherId}")
    public List<User> getCrossFriend(@PathVariable Long id, @PathVariable("otherId") Long friendId) {
        log.info("Находим общих друзей у пользователей с ID: " + id + " и " + friendId);
        return userService.getCommonFriends(id, friendId);
    }
}
