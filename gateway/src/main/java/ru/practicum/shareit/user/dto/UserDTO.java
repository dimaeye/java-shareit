package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    @Pattern(regexp = "^(?!\\s*$).+", message = "UserName can not be empty")
    private String name;
    @Email
    private String email;
}
