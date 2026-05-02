package com.taskmanager.api.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taskmanager.api.entity.Task;
import com.taskmanager.api.entity.User;
import com.taskmanager.api.repository.ProjectRepository;
import com.taskmanager.api.repository.TaskRepository;
import com.taskmanager.api.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @GetMapping("/project/{projectId}")
    public List<Task> getByProject(@PathVariable Long projectId) {
        return taskRepository.findByProjectId(projectId);
    }

    @GetMapping("/my")
    public List<Task> getMyTasks(Authentication auth) {
        User user = (User) auth.getPrincipal();
        return taskRepository.findByAssignedToId(user.getId());
    }

    @GetMapping("/overdue")
    public List<Task> getOverdue() {
        return taskRepository.findByDueDateBeforeAndStatusNot(LocalDate.now(), Task.Status.DONE);
    }

    @GetMapping("/dashboard")
    public Map<String, Object> getDashboard() {
        List<Task> all = taskRepository.findAll();
        long todo = all.stream().filter(t -> t.getStatus() == Task.Status.TODO).count();
        long inProgress = all.stream().filter(t -> t.getStatus() == Task.Status.IN_PROGRESS).count();
        long done = all.stream().filter(t -> t.getStatus() == Task.Status.DONE).count();
        List<Task> overdue = taskRepository.findByDueDateBeforeAndStatusNot(LocalDate.now(), Task.Status.DONE);
        return Map.of("todo", todo, "inProgress", inProgress, "done", done, "overdue", overdue.size());
    }

    @PostMapping
    public ResponseEntity<Task> create(@RequestBody Task task) {
        task.setCreatedAt(LocalDateTime.now());
        if (task.getStatus() == null) task.setStatus(Task.Status.TODO);
        return ResponseEntity.ok(taskRepository.save(task));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> update(@PathVariable Long id, @RequestBody Task updated) {
        return taskRepository.findById(id).map(t -> {
            if (updated.getTitle() != null) t.setTitle(updated.getTitle());
            if (updated.getDescription() != null) t.setDescription(updated.getDescription());
            if (updated.getStatus() != null) t.setStatus(updated.getStatus());
            if (updated.getDueDate() != null) t.setDueDate(updated.getDueDate());
            if (updated.getAssignedTo() != null) t.setAssignedTo(updated.getAssignedTo());
            return ResponseEntity.ok(taskRepository.save(t));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        taskRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}