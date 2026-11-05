
package com.priyanka.taskflow_backend.dto.response;

import com.priyanka.taskflow_backend.Task;

public class TaskResponse {

    private Long id;
    private String title;
    private String description;
    private String category;
    private String priority;
    private boolean completed;

    public TaskResponse(Task task) {
        this.id = task.getId();
        this.title = task.getTitle();
        this.description = task.getDescription();
        this.category = task.getCategory();
        this.priority = task.getPriority();
        this.completed = task.isCompleted();
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public String getPriority() {
        return priority;
    }

    public boolean isCompleted() {
        return completed;
    }
}
