package org.example.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {
    private Long id;
    private String text;
    private Long authorId;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}