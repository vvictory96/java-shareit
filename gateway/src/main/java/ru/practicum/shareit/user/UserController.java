package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.practicum.shareit.json.SuccessJSON;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;

import static java.lang.String.format;


@Slf4j
@Controller
@RequestMapping(path = "/users")
@Validated
public class UserController {
    private final UserClient userClient;

    public UserController(UserClient userClient) {
        this.userClient = userClient;
    }


    @PostMapping
    public ResponseEntity<Object> addUser(@Valid @RequestBody UserDto user) {
        log.info("---START ADD USER ENDPOINT---");
        return new ResponseEntity<>(userClient.addUser(user), HttpStatus.OK);
    }


    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUser(@PathVariable Long userId) {
        log.info("---START FIND USER ENDPOINT---");
        log.info("User = " + userClient.getUser(userId));
        return new ResponseEntity<>(userClient.getUser(userId), HttpStatus.OK);
    }


    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable Long userId,
                                             @RequestBody UserDto user) {
        log.info("---START UPDATE USER ENDPOINT---");
        return new ResponseEntity<>(userClient.updateUser(userId, user), HttpStatus.OK);
    }


    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long userId) {
        log.info("---START DELETE USER ENDPOINT---");
        userClient.deleteUser(userId);
        return new ResponseEntity<>(new SuccessJSON(format("User [%s] was delete", userId)), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        log.info("---START FIND ALL USERS ENDPOINT---");
        return new ResponseEntity<>(userClient.getAll(), HttpStatus.OK);
    }
}
