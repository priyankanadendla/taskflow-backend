package com.priyanka.taskflow_backend;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.priyanka.taskflow_backend.dto.request.TaskRequest;
import com.priyanka.taskflow_backend.dto.request.TaskUpdateRequest;
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