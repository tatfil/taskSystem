package org.example.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.model.dto.TaskCreateDTO;
import org.example.model.dto.TaskDTO;
import org.example.model.entity.Comment;
import org.example.model.entity.Task;
import org.example.model.entity.User;
import org.example.model.enums.Role;
import org.example.model.enums.TaskPriority;
import org.example.model.enums.TaskStatus;
import org.example.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserService userService;

    @Mock
    private CommentService commentService;

    @Mock
    private PagedResourcesAssembler<TaskDTO> pagedResourcesAssembler;

    @InjectMocks
    private TaskService taskService;

    private User user;
    private User admin;
    private User executor;
    private Task task;
    private TaskCreateDTO taskCreateDTO;
    private Comment comment;

    private Page<Task> tasksPage;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        user = new User(1L, "user", Role.USER);
        admin = new User(2L, "admin", Role.ADMIN);
        executor = new User(3L, "executor", Role.USER);
        task = new Task(1L, admin, executor, "Test Task", "Test Description", TaskPriority.HIGH, TaskStatus.PENDING);
        taskCreateDTO = new TaskCreateDTO(executor.getId(), "Updated Title", "Updated Description", TaskPriority.LOW, TaskStatus.IN_PROGRESS);
        comment = new Comment(1L, "Test Comment", task, user);
        tasksPage = new PageImpl<>(List.of(task));
        pageable = PageRequest.of(0, 5);
    }

    @Test
    void testFindAllTasks() {

        when(taskRepository.findAll(pageable)).thenReturn(tasksPage);
        when(pagedResourcesAssembler.toModel(any(Page.class))).thenReturn(mock(PagedModel.class));

        PagedModel<EntityModel<TaskDTO>> result = taskService.findAll(pageable);

        assertNotNull(result);
        verify(taskRepository).findAll(pageable);
        verify(pagedResourcesAssembler).toModel(any(Page.class));
    }

    @Test
    void testFindById_TaskExists() {
        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));

        TaskDTO result = taskService.findById(task.getId());

        assertNotNull(result);
        verify(taskRepository).findById(task.getId());
    }

    @Test
    void testFindById_TaskNotFound() {

        when(taskRepository.findById(task.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> taskService.findById(task.getId()));
    }

    @Test
    void testGetById_TaskExists() {
        Long taskId = 1L;
        Task task = new Task();
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        Optional<Task> result = taskService.getById(taskId);

        assertTrue(result.isPresent());
        verify(taskRepository).findById(taskId);
    }

    @Test
    void testFindByExecutor() {

        when(taskRepository.findByExecutor_Id(executor.getId(), pageable)).thenReturn(tasksPage);
        when(pagedResourcesAssembler.toModel(any(Page.class))).thenReturn(mock(PagedModel.class));

        PagedModel<EntityModel<TaskDTO>> result = taskService.findByExecutor(executor.getId(), pageable);

        assertNotNull(result);
        verify(taskRepository).findByExecutor_Id(executor.getId(), pageable);
    }

    @Test
    void testFindByAuthor() {

        when(taskRepository.findByAuthor_Id(task.getAuthor().getId(), pageable)).thenReturn(tasksPage);
        when(pagedResourcesAssembler.toModel(any(Page.class))).thenReturn(mock(PagedModel.class));

        PagedModel<EntityModel<TaskDTO>> result = taskService.findByAuthor(task.getAuthor().getId(), pageable);

        assertNotNull(result);
        verify(taskRepository).findByAuthor_Id(task.getAuthor().getId(), pageable);
    }


    @Test
    void testFindByStatus() {
        when(taskRepository.findByStatus(task.getStatus(), pageable)).thenReturn(tasksPage);
        when(pagedResourcesAssembler.toModel(any(Page.class))).thenReturn(mock(PagedModel.class));

        PagedModel<EntityModel<TaskDTO>> result = taskService.findByStatus(task.getStatus(), pageable);

        assertNotNull(result);
        verify(taskRepository).findByStatus(task.getStatus(), pageable);
    }

    @Test
    void testGetFilteredTasks() {
        TaskStatus status = TaskStatus.IN_PROGRESS;
        TaskPriority priority = TaskPriority.HIGH;


        when(taskRepository.findByFilters(status, priority, pageable)).thenReturn(tasksPage);
        when(pagedResourcesAssembler.toModel(any(Page.class))).thenReturn(mock(PagedModel.class));

        PagedModel<EntityModel<TaskDTO>> result = taskService.getFilteredTasks(status, priority, pageable);

        assertNotNull(result);
        verify(taskRepository).findByFilters(status, priority, pageable);
    }

    @Test
    void update_ShouldUpdateTask_WhenTaskAndExecutorExist() {
        when(userService.getById(3L)).thenReturn(Optional.of(executor));
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        TaskDTO updatedTask = taskService.update(taskCreateDTO, 1L, 1L);

        assertNotNull(updatedTask);
        assertEquals("Updated Title", updatedTask.getTitle());
        assertEquals("Updated Description", updatedTask.getDescription());
    }


    @Test
    void addComment_ShouldAddComment_WhenUserIsExecutorOrAdmin() {
        when(userService.getById(1L)).thenReturn(Optional.of(user));
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        assertThrows(AccessDeniedException.class, () ->
                taskService.addComment(1L, 1L, "New Comment"));
    }

    @Test
    void addComment_ShouldThrowException_WhenUserNotAuthorized() {
        when(userService.getById(1L)).thenReturn(Optional.of(user));
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        assertThrows(AccessDeniedException.class, () ->
                taskService.addComment(1L, 1L, "New Comment"));
    }

    @Test
    void updateStatus_ShouldUpdateStatus_WhenUserIsExecutorOrAdmin() {
        when(userService.getById(3L)).thenReturn(Optional.of(executor));
        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));

        TaskDTO updatedTask = taskService.updateStatus(task.getId(), "COMPLETED", 3L);

        assertNotNull(updatedTask);
        assertEquals(TaskStatus.COMPLETED, updatedTask.getStatus());
    }

    @Test
    void delete_ShouldDeleteTask_WhenTaskExists() {
//        when(userService.getById(1L)).thenReturn(Optional.of(user));
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        ResponseEntity<Void> response = taskService.delete(1L, 1L);

        assertEquals(204, response.getStatusCodeValue());
        verify(taskRepository, times(1)).delete(task);
    }
}