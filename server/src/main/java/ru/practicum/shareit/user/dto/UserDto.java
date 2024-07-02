package ru.practicum.shareit.user.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class UserDto {
    private long id;
    @NotNull
    private String name;
    @Email
    @NotNull
    private String email;
}
