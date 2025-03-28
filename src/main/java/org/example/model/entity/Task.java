package org.example.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.example.model.enums.TaskPriority;
import org.example.model.enums.TaskStatus;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "task")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    @ManyToOne
    @JoinColumn(name = "executor_id")
    private User executor;

    private String title;

    private String description;

    @Column(name = "priority")
    @Enumerated(EnumType.STRING)
    private TaskPriority priority;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Comment> comments = new ArrayList<>();

    @Column(name = "created_at")
    private ZonedDateTime createdAt;

    @Column(name = "updated_at")
    private ZonedDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = ZonedDateTime.now();
        updatedAt = ZonedDateTime.now();
    }
    @PreUpdate
    protected void onUpdate() {
        updatedAt = ZonedDateTime.now();
    }

    public Task(Long id, User author, User executor, String title, String description, TaskPriority priority, TaskStatus status) {
        this.id = id;
        this.author = author;
        this.executor = executor;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.status = status;
    }

    public Task(Long id, User author, User executor, String title, String description, TaskPriority priority, TaskStatus status, List<Comment> comments) {
        this.id = id;
        this.author = author;
        this.executor = executor;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.status = status;
        this.comments = comments;
    }
}
