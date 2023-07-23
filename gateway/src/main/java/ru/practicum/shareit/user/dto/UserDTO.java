package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

@Data
@Builder
@AllArgsConstructor
public class UserDTO {
    private int id;
    @Pattern(regexp = "^(?!\\s*$).+", message = "UserName can not be empty")
    private String name;
    @Email
    private String email;
}
