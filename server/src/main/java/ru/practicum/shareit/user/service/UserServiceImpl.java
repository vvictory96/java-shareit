package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ObjectExistenceException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public UserDto addUser(UserDto user) {
        return UserMapper.toUserDto(repository.save(UserMapper.toUser(user)));
    }

    @Override
    public UserDto getUser(Long id) {
        return UserMapper.toUserDto(getUserById(id));
    }

    @Override
    @Transactional
    public UserDto updateUser(Long userId, UserDto user) {
        User userToUpdate = getUserById(userId);

        userToUpdate.setEmail(user.getEmail() == null ? userToUpdate.getEmail() : user.getEmail());
        userToUpdate.setName(user.getName() == null ? userToUpdate.getName() : user.getName());

        return UserMapper.toUserDto(repository.save(userToUpdate));
    }

    @Override
    public void deleteUser(Long id) {
        repository.deleteById(id);
    }

    @Override
    public List<UserDto> getAll() {
        return repository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    private User getUserById(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ObjectExistenceException("User doesn't exists"));
    }
}