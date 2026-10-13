
package com.priyanka.taskflow_backend;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskRepository taskRepository;

    public TaskController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    // Get all tasks
    @GetMapping
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    // Add a new task
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Task createTask(@RequestBody Task task) {
        if (task.getTitle() == null || task.getTitle().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Task title cannot be empty");
        }

        task.setId(null);
        task.setTitle(task.getTitle().trim());
        if (task.getCategory() == null || task.getCategory().trim().isEmpty()) {
            task.setCategory("Work");
        }
        if (task.getPriority() == null || task.getPriority().trim().isEmpty()) {
            task.setPriority("Medium");
        }
        if (task.getDescription() == null) {
            task.setDescription("");
        }

        return taskRepository.save(task);
    }

    // Update an existing task
    @PutMapping("/{id}")
    public Task updateTask(
            @PathVariable Long id,
            @RequestBody Task updatedTask) {

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Task not found"));

        if (updatedTask.getTitle() != null && !updatedTask.getTitle().trim().isEmpty()) {
            task.setTitle(updatedTask.getTitle().trim());
        }
        if (updatedTask.getDescription() != null) {
            task.setDescription(updatedTask.getDescription());
        }
        if (updatedTask.getCategory() != null && !updatedTask.getCategory().trim().isEmpty()) {
            task.setCategory(updatedTask.getCategory().trim());
        }
        if (updatedTask.getPriority() != null && !updatedTask.getPriority().trim().isEmpty()) {
            task.setPriority(updatedTask.getPriority().trim());
        }
        task.setCompleted(updatedTask.isCompleted());

        return taskRepository.save(task);
    }

    // Delete a task
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable Long id) {
        if (!taskRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Task not found");
        }

        taskRepository.deleteById(id);
    }
}
