package org.example.repository;

import org.example.model.dto.TaskSummaryDTO;
import org.example.model.entity.Task;
import org.example.model.enums.TaskPriority;
import org.example.model.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Range;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    @EntityGraph(attributePaths = {"executor", "author"})
    Page<Task> findAll(Pageable pageable);

    Page<Task> findByExecutor_Id(Long id, Pageable pageable);


    Page<Task> findByStatus(TaskStatus status, Pageable pageable);

    Page<Task> findByAuthor_Id(Long id, Pageable pageable);

    @Query("SELECT t FROM Task t WHERE (:status IS NULL OR t.status = :status) AND (:priority IS NULL OR t.priority = :priority)")
    Page<Task> findByFilters(@Param("status") TaskStatus status,
                             @Param("priority") TaskPriority priority,
                             Pageable pageable);

    List<Task> findByIdGreaterThanOrderByIdAsc(Long afterId, Pageable pageable);

    @Query("SELECT new org.example.model.dto.TaskSummaryDTO(t.id, t.title, t.status) FROM Task t WHERE t.id = :id")
    Optional<TaskSummaryDTO> findTaskSummaryById(@Param("id") Long id);
}


