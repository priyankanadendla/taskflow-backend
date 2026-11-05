
package com.priyanka.taskflow_backend.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class TaskUpdateRequest {

    @Pattern(
        regexp = ".*\\S.*",
        message = "Task title cannot be blank"
    )
    @Size(max = 150, message = "Task title cannot exceed 150 characters")
    private String title;

    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;

    @Size(max = 50, message = "Category cannot exceed 50 characters")
    private String category;

    @Pattern(
        regexp = "(?i)^(LOW|MEDIUM|HIGH)$",
        message = "Priority must be Low, Medium, or High"
    )
    private String priority;

    private Boolean completed;

    public TaskUpdateRequest() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }
}
