package ru.practicum.shareit.user.dto;
import lombok.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@Builder
public class UserDto {
    private Long id;
    @NotBlank
    private String name;
    @Email
    @NotBlank
    private String email;
}
