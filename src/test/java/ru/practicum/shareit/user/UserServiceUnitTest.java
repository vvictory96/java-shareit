package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ObjectExistenceException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceUnitTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userService;

    private User user1;

    @BeforeEach
    void beforeEach() {
        user1 = User.builder()
                .id(1)
                .name("user1 name")
                .email("user1@testmail.com")
                .build();

    }

    @Test
    public void getUser_shouldReturnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));

        UserDto result = userService.getUser(1L);

        assertUsers(user1, result);
    }

    @Test
    public void updateUserName() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));

        UserDto changedUser = UserDto.builder()
                .id(1)
                .name("user1 changed name")
                .email(null)
                .build();

        when(userRepository.save(any())).thenReturn(user1);

        UserDto result = userService.updateUser(1L, changedUser);

        assertUsers(user1, result);
    }

    @Test
    public void updateUserName_ForWrongUserId_ShouldThrowException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        ObjectExistenceException exception = assertThrows(ObjectExistenceException.class, () ->
                userService.updateUser(99L, UserDto.builder().build()));

        assertThat(exception.getMessage(), is("User doesn't exists"));
    }

    @Test
    public void deleteUserTest() {
        userService.deleteUser(1L);
    }

    @Test
    public void getAllTest() {
        when(userRepository.findAll()).thenReturn(List.of(user1));

        List<UserDto> result = userService.getAll();

        assertThat(result.size(), is(1));
        assertThat(result.get(0).getId(), is(1L));
        assertThat(result.get(0).getName(), is("user1 name"));
        assertThat(result.get(0).getEmail(), is("user1@testmail.com"));
    }

    private void assertUsers(User input, UserDto output) {
        assertThat(input.getId(), is(output.getId()));
        assertThat(input.getEmail(), is(output.getEmail()));
        assertThat(input.getName(), is(output.getName()));
    }
}
