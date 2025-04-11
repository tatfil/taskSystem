package org.example.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.model.dto.TaskCreateDTO;
import org.example.model.dto.TaskDTO;
import org.example.model.dto.TaskSummaryDTO;
import org.example.model.entity.Comment;
import org.example.model.entity.Task;

import org.example.model.entity.User;
import org.example.model.enums.Role;
import org.example.model.enums.TaskPriority;
import org.example.model.enums.TaskStatus;
import org.example.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class TaskService {

    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);
    private final TaskRepository taskRepository;
    private final CommentService commentService;
    private final UserService userService;

    private final PagedResourcesAssembler<TaskDTO> pagedResourcesAssembler;

    public PagedModel<EntityModel<TaskDTO>> findAll(Pageable pageable) {
        logger.info("Fetching all tasks");

        Page<TaskDTO> tasksDTO = taskRepository.findAll(pageable).map(TaskDTO::fromEntity);

        logger.info("Found {} all tasks", tasksDTO.getTotalElements());
        return pagedResourcesAssembler.toModel(tasksDTO);
    }

    public TaskDTO findById(Long id) {
        logger.info("Fetching tasks by id {}", id);

        TaskDTO taskDTO = taskRepository.findById(id).map(TaskDTO::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException("Task not found with ID: " + id));


        logger.info("Task with id {} found", id);
        return taskDTO;
    }

    public Optional<Task> getById(Long id) {
        logger.info("Fetching tasks by id {}", id);

        Optional<Task> task = taskRepository.findById(id);

        logger.info("Task with id {} found", id);
        return task;
    }
    public PagedModel<EntityModel<TaskDTO>> findByExecutor(Long id, Pageable pageable) {
        logger.info("Fetching tasks for executor {}", id);

        Page<TaskDTO> tasksDTO = taskRepository.findByExecutor_Id(id, pageable).map(TaskDTO::fromEntity);

        logger.info("Found {} tasks for executor {}", tasksDTO.getTotalElements(), id);
        return pagedResourcesAssembler.toModel(tasksDTO);
    }

    public PagedModel<EntityModel<TaskDTO>> findByAuthor(Long id, Pageable pageable) {
        logger.info("Fetching tasks for author {}", id);

        Page<TaskDTO> tasksDTO = taskRepository.findByAuthor_Id(id, pageable).map(TaskDTO::fromEntity);

        logger.info("Found {} tasks by author {}", tasksDTO.getTotalElements(), id);
        return pagedResourcesAssembler.toModel(tasksDTO);

    }

    public PagedModel<EntityModel<TaskDTO>> findByStatus(TaskStatus status, Pageable pageable) {
        logger.info("Fetching tasks by status {}", status);

        Page<TaskDTO> tasksDTO = taskRepository.findByStatus(status, pageable).map(TaskDTO::fromEntity);

        logger.info("Found {} tasks by status {}", tasksDTO.getTotalElements(), status);
        return pagedResourcesAssembler.toModel(tasksDTO);

    }

    public PagedModel<EntityModel<TaskDTO>> getFilteredTasks(TaskStatus status, TaskPriority priority, Pageable pageable) {
        logger.info("Fetching tasks filtered by status {} or/and priority {} ", status, priority);

        Page<TaskDTO> tasksDTO = taskRepository.findByFilters(status, priority, pageable).map(TaskDTO::fromEntity);

        logger.info("Found {} tasks filtered by status {} or/and priority {} ", tasksDTO.getTotalElements(), status, priority);
        return pagedResourcesAssembler.toModel(tasksDTO);
    }

    @Transactional
    public TaskDTO save(TaskCreateDTO taskCreateDTO, Long authorId) {
        logger.info("Saving task {} for author {} ", taskCreateDTO.getTitle(), authorId);

        User author = userService.getById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("Author not found with ID: " + authorId));

        User executor = userService.getById(taskCreateDTO.getExecutorId())
                .orElseThrow(() -> new EntityNotFoundException("Executor not found with ID: " + taskCreateDTO.getExecutorId()));

        Task task = Task.builder()
                .author(author)
                .executor(executor)
                .title(taskCreateDTO.getTitle())
                .description(taskCreateDTO.getDescription())
                .priority(taskCreateDTO.getTaskPriority())
                .status(taskCreateDTO.getTaskStatus())
                .build();


        Task savedTask = taskRepository.save(task);
        logger.info("Task with title {} with id {} saved for author {}", taskCreateDTO.getTitle(), savedTask.getId(), authorId);

        return TaskDTO.fromEntity(savedTask);
    }

    @Transactional
    public TaskDTO update(TaskCreateDTO taskCreateDTO, Long taskId, Long userId) {
        logger.info("Updating task {} for author {} ", taskId, userId);


        User executor = userService.getById(taskCreateDTO.getExecutorId())
                .orElseThrow(() -> new EntityNotFoundException("Executor not found with ID: " + taskCreateDTO.getExecutorId()));

        Task task = getById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found with ID: " + taskId));

        task.setExecutor(executor);
        task.setTitle(taskCreateDTO.getTitle());
        task.setDescription(taskCreateDTO.getDescription());
        task.setPriority(taskCreateDTO.getTaskPriority());
        task.setStatus(taskCreateDTO.getTaskStatus());

        logger.info("Task with id {} updated for author {}", taskId, userId);
        return TaskDTO.fromEntity(task);
    }

    @Transactional
    public Comment addComment(Long taskId, Long userId, String string) {
        logger.info("Adding a comment for task {} by user {}", taskId, userId);

        User user = userService.getById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        Task task = getById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found with ID: " + taskId));

        //Either ADMIN or executor can add a comment to task
        if (!task.getExecutor().equals(user) && user.getRole() == Role.USER)
            throw new AccessDeniedException("User ID: " + userId + " has no authority to add a comment to task with ID: " + taskId);

        Comment comment = Comment.builder()
                .task(task)
                .text(string)
                .author(user)
                .build();

        Comment savedComment = commentService.save(comment);

        logger.info("Comment with id {} saved for author {}", savedComment.getId(), userId);

        return savedComment;
    }

    public TaskDTO updateStatus(Long taskId, String status, Long userId) {
        logger.info("Updating task status {} for task {} by user {}", status, taskId, userId);

        User user = userService.getById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        Task task = getById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found with ID: " + taskId));

        //Either ADMIN or executor can change the status for task
        if (!task.getExecutor().equals(user) && user.getRole() == Role.USER)
            throw new AccessDeniedException("User ID: " + userId + " has no authority to change the status for task with ID: " + taskId);


        task.setStatus(TaskStatus.valueOf(status));
        taskRepository.save(task);

        logger.info("Status was updated for task {} by user {}", taskId, userId);
        return TaskDTO.fromEntity(task);

    }


    public ResponseEntity<Void> delete(Long taskId, Long userId) {
        logger.info("Deleting task with id: {} by user {}", taskId, userId);

        Task task = getById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found with ID: " + taskId));

        taskRepository.delete(task);

        logger.warn("Task with id: {} is deleted by user {}", taskId, userId);

        return ResponseEntity.noContent().build();
    }


    //---------------for GraphQl--------------------------
    public List<TaskDTO> findFirstPage(Pageable pageable) {
        Page<TaskDTO> tasksPage = taskRepository.findAll(pageable).map(TaskDTO::fromEntity);
        return tasksPage.getContent();
    }

    public List<TaskDTO> findAllAfterId(Long afterId, Pageable pageable) {
        List<Task> tasks = taskRepository.findByIdGreaterThanOrderByIdAsc(afterId, pageable);
        return tasks.stream().map(TaskDTO::fromEntity).toList();
    }

    public TaskSummaryDTO getSummaryById(Long taskId) {

        logger.info("Fetching tasks by id {} using GraphQL", taskId);

        TaskSummaryDTO taskSummaryDTO = taskRepository.findTaskSummaryById(taskId)
                    .orElseThrow(() -> new EntityNotFoundException("Task not found"));

        logger.info("Task with id {} found using GraphQL", taskId);
        return taskSummaryDTO;
    }
    //------------------------------------------------------

}
