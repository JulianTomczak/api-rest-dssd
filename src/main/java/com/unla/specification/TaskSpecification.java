package com.unla.specification;

import java.time.LocalDate;

import org.springframework.data.jpa.domain.Specification;

import com.unla.entities.Task;

public class TaskSpecification {
    public static Specification<Task> hasTitle(String title) {
        return (root, query, cb) ->
            title == null ? null : cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }

    public static Specification<Task> isCompleted(Boolean completed) {
        return (root, query, cb) ->
            completed == null ? null : cb.equal(root.get("completed"), completed);
    }

    public static Specification<Task> dueBefore(LocalDate date) {
        return (root, query, cb) ->
            date == null ? null : cb.lessThan(root.get("dueDate"), date);
    }

    public static Specification<Task> belongsToUser(Integer userId) {
        return (root, query, cb) ->
            userId == null ? null : cb.equal(root.get("user").get("id"), userId);
    }
}
