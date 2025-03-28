package org.example.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.example.model.enums.TaskPriority;
import org.example.model.enums.TaskStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskCreateDTO {

    private Long executorId;

    @Size(min = 1, max = 100, message = "Task title must be between 1 and 100 characters.")
    @NotBlank(message = "Task title can not be empty.")
    private String title;

    @Size(max = 1000, message = "Description must be less than 1000 characters.")
    private String description;

    @Schema(description = "Enter task priority", example = "LOW")
    @NotNull(message = "task priority cannot be blank.")
    private TaskPriority taskPriority;

    @Schema(description = "Enter task status", example = "PENDING")
    @NotNull(message = "task status cannot be blank.")
    private TaskStatus taskStatus;
}
