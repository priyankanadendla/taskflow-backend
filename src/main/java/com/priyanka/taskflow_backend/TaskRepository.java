
package com.priyanka.taskflow_backend;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TaskRepository extends JpaRepository<Task, Long> {

    // Filter tasks by category, priority and completion status
    @Query("""
            SELECT t FROM Task t
            WHERE (:category IS NULL OR LOWER(t.category) = LOWER(:category))
            AND (:priority IS NULL OR LOWER(t.priority) = LOWER(:priority))
            AND (:completed IS NULL OR t.completed = :completed)
            """)
    List<Task> findByFilters(
            @Param("category") String category,
            @Param("priority") String priority,
            @Param("completed") Boolean completed
    );

    // Search tasks by title (case-insensitive)
    List<Task> findByTitleContainingIgnoreCase(String title);

    // Search tasks by description (case-insensitive)
    List<Task> findByDescriptionContainingIgnoreCase(String description);
}
