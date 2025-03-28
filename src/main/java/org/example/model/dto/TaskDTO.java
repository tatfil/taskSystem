package org.example.model.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.model.entity.Task;
import org.example.model.enums.TaskPriority;
import org.example.model.enums.TaskStatus;


import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTO {
    private Long id;
    private Long authorId;
    private Long executorId;
    private String title;
    private String description;
    private TaskPriority priority;
    private TaskStatus status;
    private List<CommentDTO> comments = new ArrayList<>();
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;

    public static TaskDTO fromEntity(Task task) {

        return new TaskDTO(
                task.getId(),
                task.getAuthor().getId(),
                task.getExecutor().getId(),
                task.getTitle(),
                task.getDescription(),
                task.getPriority(),
                task.getStatus(),
                task.getComments() == null || task.getComments().isEmpty()
                        ? new ArrayList<>()
                        : mapToCommentDTO(task),
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }

    public static List<CommentDTO> mapToCommentDTO(Task task) {
        return task.getComments().stream()
                .map(comment -> new CommentDTO(
                        comment.getId(),
                        comment.getText(),
                        comment.getAuthor().getId(),
                        comment.getCreatedAt(),
                        comment.getUpdatedAt()
                ))
                .collect(Collectors.toList());
    }
}
