package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class CommentDTO {
    private int id;
    @NotBlank
    private String text;
    private String authorName;
    private LocalDateTime created;
}
