
package com.priyanka.taskflow_backend;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Transactional(readOnly = true)
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Task createTask(Task task) {

        if (task.getTitle() == null || task.getTitle().trim().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Task title cannot be empty"
            );
        }

        task.setId(null);
        task.setTitle(task.getTitle().trim());

        if (task.getCategory() == null || task.getCategory().trim().isEmpty()) {
            task.setCategory("Work");
        } else {
            task.setCategory(task.getCategory().trim());
        }

        if (task.getPriority() == null || task.getPriority().trim().isEmpty()) {
            task.setPriority("Medium");
        } else {
            task.setPriority(task.getPriority().trim());
        }

        if (task.getDescription() == null) {
            task.setDescription("");
        }

        return taskRepository.save(task);
    }

    public Task updateTask(Long id, Task updatedTask) {

        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Task not found"
                ));

        if (updatedTask.getTitle() != null
                && !updatedTask.getTitle().trim().isEmpty()) {
            existingTask.setTitle(updatedTask.getTitle().trim());
        }

        if (updatedTask.getDescription() != null) {
            existingTask.setDescription(updatedTask.getDescription());
        }

        if (updatedTask.getCategory() != null
                && !updatedTask.getCategory().trim().isEmpty()) {
            existingTask.setCategory(updatedTask.getCategory().trim());
        }

        if (updatedTask.getPriority() != null
                && !updatedTask.getPriority().trim().isEmpty()) {
            existingTask.setPriority(updatedTask.getPriority().trim());
        }

        existingTask.setCompleted(updatedTask.isCompleted());

        return taskRepository.save(existingTask);
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
