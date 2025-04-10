package org.example.controller;

import org.example.model.dto.TaskDTO;
import org.example.model.entity.Task;
import org.example.service.TaskService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SchemaMapping
@Controller
public class TaskGraphQLController {

    private final TaskService taskService;

    public TaskGraphQLController(TaskService taskService) {
        this.taskService = taskService;
    }

    @QueryMapping
    public TaskDTO taskById(@Argument Long id) {
        return taskService.findById(id);
    }
}