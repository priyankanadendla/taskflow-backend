package com.priyanka.taskflow_backend.dto.response;

import java.util.List;

public class TaskPageResponse {

    private List<TaskResponse> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean last;

    public TaskPageResponse(
            List<TaskResponse> content,
            int page,
            int size,
            long totalElements,
            int totalPages,
            boolean last) {

        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.last = last;
    }

    public List<TaskResponse> getContent() {
        return content;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public boolean isLast() {
        return last;
    }
}