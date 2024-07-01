package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ObjectExistenceException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto addUser(UserDto user) {
        log.info(format("Create user: %s", user));
        return UserMapper.toUserDto(userRepository.save(UserMapper.toUser(user)));
    }

    @Override
    public UserDto getUser(Long id) {
        log.info(format("Get user by id: %s", id));
        log.info("User: {}", UserMapper.toUserDto(getUserById(id)));
        return UserMapper.toUserDto(getUserById(id));
    }

    @Override
    @Transactional
    public UserDto updateUser(Long userId, UserDto user) {
        log.info(format("Start update idUser = [%s]", user.getId()));
        User userToUpdate = getUserById(userId);

        userToUpdate.setEmail(user.getEmail() == null ? userToUpdate.getEmail() : user.getEmail());
        userToUpdate.setName(user.getName() == null ? userToUpdate.getName() : user.getName());
        log.info("User after update: {}", getUser(userId));
        return UserMapper.toUserDto(userRepository.save(userToUpdate));
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public List<UserDto> getAll() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    private User getUserById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ObjectExistenceException("User doesn't exists"));
    }
}
