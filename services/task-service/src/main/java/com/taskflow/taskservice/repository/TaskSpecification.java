package com.taskflow.taskservice.repository;

import com.taskflow.common.enums.TaskPriority;
import com.taskflow.common.enums.TaskStatus;
import com.taskflow.taskservice.entity.Task;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TaskSpecification {

    public static Specification<Task> filterTasks(
            Long projectId,
            Long sprintId,
            Long assigneeId,
            TaskStatus status,
            TaskPriority priority,
            LocalDate dueDate,
            String search
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (projectId != null) predicates.add(cb.equal(root.get("projectId"), projectId));
            if (sprintId != null) predicates.add(cb.equal(root.get("sprintId"), sprintId));
            if (assigneeId != null) predicates.add(cb.equal(root.get("assigneeId"), assigneeId));
            if (status != null) predicates.add(cb.equal(root.get("status"), status));
            if (priority != null) predicates.add(cb.equal(root.get("priority"), priority));
            if (dueDate != null) predicates.add(cb.equal(root.get("dueDate"), dueDate));

            if (search != null && !search.trim().isEmpty()) {
                String pattern = "%" + search.trim().toLowerCase() + "%";
                Predicate titleLike = cb.like(cb.lower(root.get("title")), pattern);
                Predicate descLike = cb.like(cb.lower(root.get("description")), pattern);
                predicates.add(cb.or(titleLike, descLike));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
