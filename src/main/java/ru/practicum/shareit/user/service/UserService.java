package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto addUser(UserDto user);

    UserDto getUser(Long id);

    UserDto updateUser(Long userId, UserDto user);

    void deleteUser(Long id);

    List<UserDto> getAll();
}
