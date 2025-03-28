package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.exception.ErrorResponse;
import org.example.model.dto.TaskCreateDTO;
import org.example.model.dto.TaskDTO;
import org.example.model.entity.Comment;
import org.example.model.entity.User;
import org.example.model.enums.TaskPriority;
import org.example.model.enums.TaskStatus;
import org.example.service.TaskService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/tasks")
public class TaskController {

    private final TaskService taskService;

    @Operation(summary = "Get all tasks")
    @Tag(name = "task CRUD")
    @ApiResponse(responseCode = "200", description = "All tasks found",
            content = {@io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
                    schema = @Schema(implementation = TaskDTO.class))})
    @ApiResponse(responseCode = "400", description = "Invalid parameters",
            content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "403", description = "Forbidden",
            content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Entity not found",
            content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server error",
            content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<TaskDTO>>> getAllTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(taskService.findAll(pageable));
    }

    @Operation(summary = "Get by id")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @Tag(name = "task CRUD")
    @ApiResponse(responseCode = "200", description = "Get task by id",
            content = {@io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
                    schema = @Schema(implementation = TaskDTO.class))})
    @ApiResponse(responseCode = "400", description = "Invalid parameters",
            content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "403", description = "Forbidden",
            content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Entity not found",
            content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server error",
            content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/{taskId}")
    public ResponseEntity<TaskDTO> getById(
            @PathVariable Long taskId) {
        return ResponseEntity.ok(taskService.findById(taskId));
    }

    @Operation(summary = "Get by executor")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @Tag(name = "task CRUD")
    @ApiResponse(responseCode = "200", description = "Get task by executor",
            content = {@io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
                    schema = @Schema(implementation = TaskDTO.class))})
    @ApiResponse(responseCode = "400", description = "Invalid parameters",
            content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "403", description = "Forbidden",
            content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Entity not found",
            content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server error",
            content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/executor/{executorId}")
    public ResponseEntity<PagedModel<EntityModel<TaskDTO>>> getByExecutor(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @PathVariable Long executorId) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(taskService.findByExecutor(executorId ,pageable));
    }

    @Operation(summary = "Get by author")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @Tag(name = "task CRUD")
    @ApiResponse(responseCode = "200", description = "Get task by author",
            content = {@io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
                    schema = @Schema(implementation = TaskDTO.class))})
    @ApiResponse(responseCode = "400", description = "Invalid parameters",
            content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "403", description = "Forbidden",
            content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Entity not found",
            content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server error",
            content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/author/{authorId}")
    public ResponseEntity<PagedModel<EntityModel<TaskDTO>>> getByAuthor(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @PathVariable Long authorId) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(taskService.findByAuthor(authorId ,pageable));
    }


    @Operation(summary = "Get by status")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @ApiResponse(responseCode = "200", description = "Get task by status",
            content = {@io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
                    schema = @Schema(implementation = TaskDTO.class))})
    @ApiResponse(responseCode = "400", description = "Invalid parameters",
            content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "403", description = "Forbidden",
            content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Entity not found",
            content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server error",
            content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @Tag(name = "task CRUD")
    @GetMapping("/status/{status}")
    public ResponseEntity<PagedModel<EntityModel<TaskDTO>>> getByStatus(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @PathVariable TaskStatus status) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(taskService.findByStatus(status, pageable));
    }

    @Operation(summary = "Filter by status or/and priority")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @Tag(name = "task CRUD")
    @ApiResponse(responseCode = "200", description = "Filter by status or/and priority",
            content = {@io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
                    schema = @Schema(implementation = TaskDTO.class))})
    @ApiResponse(responseCode = "400", description = "Invalid parameters",
            content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "403", description = "Forbidden",
            content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Entity not found",
            content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server error",
            content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/filter/")
    public ResponseEntity<PagedModel<EntityModel<TaskDTO>>> filterTasks(
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) TaskPriority priority,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(taskService.getFilteredTasks(status, priority, pageable));
    }


    @Operation(summary = "Create a new task")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Tag(name = "task CRUD")
    @ApiResponse(responseCode = "200", description = "Create a new task",
            content = {@io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
                    schema = @Schema(implementation = TaskDTO.class))})
    @ApiResponse(responseCode = "400", description = "Invalid parameters",
            content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "403", description = "Forbidden",
            content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Entity not found",
            content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server error",
            content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping
    public ResponseEntity<TaskDTO> createTask(@RequestBody @Valid TaskCreateDTO taskCreateDTO,
                                              @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(taskService.save(taskCreateDTO, user.getId()));
    }

    @Operation(summary = "Update task")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Tag(name = "task CRUD")
    @ApiResponse(responseCode = "200", description = "Update task",
            content = {@io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
                    schema = @Schema(implementation = TaskDTO.class))})
    @ApiResponse(responseCode = "400", description = "Invalid parameters",
            content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "403", description = "Forbidden",
            content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Entity not found",
            content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server error",
            content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @PutMapping("/{taskId}")
    public ResponseEntity<TaskDTO> updateTask(@RequestBody @Valid TaskCreateDTO taskCreateDTO,
                                              @PathVariable Long taskId,
                                              @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(taskService.update(taskCreateDTO, taskId, user.getId()));
    }


    @Operation(summary = "Update status")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @Tag(name = "task CRUD")
    @ApiResponse(responseCode = "200", description = "Update status",
            content = {@io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
                    schema = @Schema(implementation = TaskDTO.class))})
    @ApiResponse(responseCode = "400", description = "Invalid parameters",
            content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "403", description = "Forbidden",
            content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Entity not found",
            content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server error",
            content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @PutMapping("/{taskId}/status")
    public ResponseEntity<TaskDTO> updateStatus(@PathVariable Long taskId,
                                             @RequestParam String status,
                                             @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(taskService.updateStatus(taskId, status, user.getId()));
    }

    @Operation(summary = "Add a comment")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @Tag(name = "comment CRUD")
    @ApiResponse(responseCode = "200", description = "Add a comment",
            content = {@io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
                    schema = @Schema(implementation = TaskDTO.class))})
    @ApiResponse(responseCode = "400", description = "Invalid parameters",
            content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "403", description = "Forbidden",
            content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Entity not found",
            content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server error",
            content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @PutMapping("/{id}/add-comment")
    public ResponseEntity<Comment> addComment(
            @PathVariable("id") Long taskId,
            @RequestParam("comment") String comment,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(taskService.addComment(taskId, user.getId(), comment));
    }

    @Operation(summary = "Delete a task")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Tag(name = "task CRUD")
    @ApiResponse(responseCode = "200", description = "Delete a task",
            content = {@io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
                    schema = @Schema(implementation = TaskDTO.class))})
    @ApiResponse(responseCode = "400", description = "Invalid parameters",
            content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "403", description = "Forbidden",
            content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Entity not found",
            content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server error",
            content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable Long taskId,
            @AuthenticationPrincipal User user) {
        return taskService.delete(taskId, user.getId());
    }
}
