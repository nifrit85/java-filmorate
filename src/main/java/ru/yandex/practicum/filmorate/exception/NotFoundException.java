package ru.yandex.practicum.filmorate.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String type, long id) {
        super(type + " c id= " + id + " не найден");
    }
}
