package com.priyanka.taskflow_backend;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import com.priyanka.taskflow_backend.dto.request.TaskRequest;
import com.priyanka.taskflow_backend.dto.request.TaskUpdateRequest;
import com.priyanka.taskflow_backend.dto.response.TaskPageResponse;
import com.priyanka.taskflow_backend.dto.response.TaskResponse;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public List<TaskResponse> getAllTasks() {
        return taskService.getAllTasks();
    }

    // Filter tasks by category, priority and completion status
    @GetMapping("/filter")
    public List<TaskResponse> getFilteredTasks(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) Boolean completed) {

        return taskService.getFilteredTasks(
                category,
                priority,
                completed);
    }

    // Get paginated and sorted tasks
    @GetMapping("/paginated")
    public TaskPageResponse getPaginatedTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        return taskService.getPaginatedTasks(
                page, size, sortBy, direction);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse createTask(
            @Valid @RequestBody TaskRequest request) {
        return taskService.createTask(request);
    }

    @PutMapping("/{id}")
    public TaskResponse updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskUpdateRequest request) {
        return taskService.updateTask(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
    }
}