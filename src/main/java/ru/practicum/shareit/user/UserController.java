package ru.practicum.shareit.user;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.json.SuccessJSON;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

import static java.lang.String.format;


@Slf4j
@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @SneakyThrows
    @PostMapping
    public ResponseEntity<UserDto> addUser(@Valid @RequestBody UserDto user) {
        log.info("---START ADD USER ENDPOINT---");
        return new ResponseEntity<>(userService.addUser(user), HttpStatus.OK);
    }

    @SneakyThrows
    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long userId) {
        log.info("---START FIND USER ENDPOINT---");
        log.info("User = " + userService.getUser(userId));
        return new ResponseEntity<>(userService.getUser(userId), HttpStatus.OK);
    }

    @SneakyThrows
    @PatchMapping("/{userId}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long userId, @RequestBody UserDto user) {
        log.info("---START UPDATE USER ENDPOINT---");
        return new ResponseEntity<>(userService.updateUser(userId, user), HttpStatus.OK);
    }

    @SneakyThrows
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        log.info("---START DELETE USER ENDPOINT---");
        userService.deleteUser(userId);
        return new ResponseEntity<>(new SuccessJSON(format("User [%s] was delete", userId)), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAll() {
        log.info("---START FIND ALL USERS ENDPOINT---");
        return new ResponseEntity<>(userService.getAll(), HttpStatus.OK);
    }
}
