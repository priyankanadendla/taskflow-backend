
package com.priyanka.taskflow_backend;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.priyanka.taskflow_backend.dto.request.TaskRequest;
import com.priyanka.taskflow_backend.dto.request.TaskUpdateRequest;
import com.priyanka.taskflow_backend.dto.response.TaskPageResponse;
import com.priyanka.taskflow_backend.dto.response.TaskResponse;

@Service
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getAllTasks() {
        return taskRepository.findAll()
                .stream()
                .map(TaskResponse::new)
                .toList();
    }

    // Search tasks by title (case-insensitive)
    @Transactional(readOnly = true)
    public List<TaskResponse> searchTasks(String title) {

        if (title == null || title.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Search title cannot be empty"
            );
        }

        return taskRepository
                .findByTitleContainingIgnoreCase(title.trim())
                .stream()
                .map(TaskResponse::new)
                .toList();
    }

    // Filter tasks by category, priority and completion status
    @Transactional(readOnly = true)
    public List<TaskResponse> getFilteredTasks(
            String category,
            String priority,
            Boolean completed) {

        category = (category == null || category.isBlank())
                ? null : category.trim();

        priority = (priority == null || priority.isBlank())
                ? null : priority.trim();

        return taskRepository.findByFilters(
                        category,
                        priority,
                        completed)
                .stream()
                .map(TaskResponse::new)
                .toList();
    }

    // Get paginated and sorted tasks
    @Transactional(readOnly = true)
    public TaskPageResponse getPaginatedTasks(
            int page, int size, String sortBy, String direction) {

        if (page < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Page number cannot be negative"
            );
        }

        if (size < 1 || size > 100) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Page size must be between 1 and 100"
            );
        }

        List<String> allowedSortFields = List.of(
                "id", "title", "category", "priority", "completed"
        );

        if (sortBy == null || !allowedSortFields.contains(sortBy.trim())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid sort field"
            );
        }

        if (direction == null || direction.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Direction must be 'asc' or 'desc'"
            );
        }

        Sort.Direction sortDirection;

        try {
            sortDirection = Sort.Direction.fromString(direction.trim());
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Direction must be 'asc' or 'desc'"
            );
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(sortDirection, sortBy.trim())
        );

        Page<Task> taskPage = taskRepository.findAll(pageable);

        List<TaskResponse> content = taskPage.getContent()
                .stream()
                .map(TaskResponse::new)
                .toList();

        return new TaskPageResponse(
                content,
                taskPage.getNumber(),
                taskPage.getSize(),
                taskPage.getTotalElements(),
                taskPage.getTotalPages(),
                taskPage.isLast()
        );
    }

    public TaskResponse createTask(TaskRequest request) {

        Task task = new Task();

        task.setTitle(request.getTitle().trim());

        task.setDescription(
                request.getDescription() == null
                        ? ""
                        : request.getDescription().trim()
        );

        task.setCategory(
                request.getCategory() == null
                        || request.getCategory().isBlank()
                        ? "Work"
                        : request.getCategory().trim()
        );

        task.setPriority(
                request.getPriority() == null
                        || request.getPriority().isBlank()
                        ? "Medium"
                        : request.getPriority().trim()
        );

        task.setCompleted(false);

        return new TaskResponse(taskRepository.save(task));
    }

    public TaskResponse updateTask(Long id, TaskUpdateRequest request) {

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Task not found"
                ));

        if (request.getTitle() != null) {
            task.setTitle(request.getTitle().trim());
        }

        if (request.getDescription() != null) {
            task.setDescription(request.getDescription().trim());
        }

        if (request.getCategory() != null) {
            task.setCategory(request.getCategory().trim());
        }

        if (request.getPriority() != null) {
            task.setPriority(request.getPriority().trim());
        }

        if (request.getCompleted() != null) {
            task.setCompleted(request.getCompleted());
        }

        return new TaskResponse(taskRepository.save(task));
    }

    public void deleteTask(Long id) {

        if (!taskRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Task not found"
            );
        }

        taskRepository.deleteById(id);
    }
}
