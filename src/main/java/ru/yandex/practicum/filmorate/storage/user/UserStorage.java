package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.Collection;

@RestController
public interface UserStorage {

    @PostMapping(value = "/users")
    User create(@Valid @RequestBody User user);

    @PutMapping(value = "/users")
    User update(@Valid @RequestBody User user);

    @GetMapping("/users")
    Collection<User> findAll();
}
