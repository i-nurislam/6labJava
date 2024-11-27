package com.example.todolist.persist.repo;

import com.example.todolist.persist.entity.Task;
import com.example.todolist.repr.TaskRepr;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface ToDoRepository extends CrudRepository<Task, Long> {
    @Query("SELECT new com.example.todolist.repr.TaskRepr(t) FROM Task t " +
            "WHERE t.user.id = :userId")
    List<TaskRepr> findToDosByUserId(@Param("userId") Long userId);

    @Query(value = "SELECT t FROM Task t WHERE t.user.id = :userId AND t.description LIKE %:keyword%",
            countQuery = "SELECT COUNT(t) FROM Task t WHERE t.user.id = :userId AND t.description LIKE %:keyword%")
    Page<Task> findByUserIdAndKeyword(@Param("userId") Long userId, @Param("keyword") String keyword, Pageable pageable);
    Page<Task> findByUserIdAndDescriptionContainingIgnoreCase(Long userId, String description, Pageable pageable);


}
