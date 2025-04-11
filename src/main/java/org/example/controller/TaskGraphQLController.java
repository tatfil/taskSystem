package org.example.controller;


import org.example.model.dto.TaskDTO;
import org.example.model.dto.TaskSummaryDTO;
import org.example.model.dto.UpdateTaskStatusInput;
import org.example.model.entity.Task;
import org.example.model.entity.User;
import org.example.model.pagination.PageInfo;
import org.example.model.pagination.TaskConnection;
import org.example.model.pagination.TaskEdge;
import org.example.service.TaskService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;


//@SchemaMapping
@Controller
public class TaskGraphQLController {

    private final TaskService taskService;

    public TaskGraphQLController(TaskService taskService) {
        this.taskService = taskService;
    }

    @QueryMapping
    public TaskConnection tasks(@Argument int first, @Argument String after) {
        Pageable pageable = PageRequest.of(0, first);
        List<TaskDTO> taskDTOs;

        if (after != null) {
            Long afterId = decodeCursor(after);
            taskDTOs = taskService.findAllAfterId(afterId, pageable);
        } else {
            taskDTOs = taskService.findFirstPage(pageable);
        }

        List<TaskEdge> edges = taskDTOs.stream()
                .map(taskDTO -> new TaskEdge(encodeCursor(taskDTO.getId()), taskDTO))
                .collect(Collectors.toList());

        PageInfo pageInfo = new PageInfo(
                edges.size() == first, // hasNextPage (not precise unless you fetch one extra)
                after != null, // hasPreviousPage
                edges.isEmpty() ? null : edges.get(0).getCursor(),
                edges.isEmpty() ? null : edges.get(edges.size() - 1).getCursor()
        );

        return new TaskConnection(edges, pageInfo);
    }


    @QueryMapping
    public TaskSummaryDTO getTaskSummaryById(@Argument Long taskId) {
        return taskService.getSummaryById(taskId);
    }

    @QueryMapping
    public TaskDTO taskById(@Argument Long id) {
        return taskService.findById(id);
    }


    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @MutationMapping
    public TaskDTO updateTaskStatus(@Argument("input") UpdateTaskStatusInput input,
                                    @AuthenticationPrincipal User user) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = 0L;

        if (principal instanceof User userDetails) {
            userId = userDetails.getId();
        }

//        return taskService.updateStatus(input.getTaskId(), input.getStatus().name(), userId);
        return taskService.updateStatus(input.getTaskId(), input.getStatus().name(), user.getId());
    }

    private String encodeCursor(Long id) {
        return Base64.getEncoder().encodeToString(id.toString().getBytes());
    }

    private Long decodeCursor(String cursor) {
        byte[] decodedBytes = Base64.getDecoder().decode(cursor);
        return Long.parseLong(new String(decodedBytes));
    }

    private int getPageNumber(String afterCursor) {
        if (afterCursor == null || afterCursor.isEmpty()) {
            return 0;
        }

        // Decode base64 cursor string (which you encoded as "cursor:{pageNumber}")
        byte[] decodedBytes = Base64.getDecoder().decode(afterCursor);
        String decoded = new String(decodedBytes, StandardCharsets.UTF_8);

        if (decoded.startsWith("cursor:")) {
            String numberPart = decoded.substring("cursor:".length());
            return Integer.parseInt(numberPart);
        }

        return 0;
    }



}