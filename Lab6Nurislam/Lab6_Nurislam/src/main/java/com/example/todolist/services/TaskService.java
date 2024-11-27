package com.example.todolist.services;

import com.example.todolist.persist.entity.Task;
import com.example.todolist.persist.entity.User;
import com.example.todolist.persist.repo.ToDoRepository;
import com.example.todolist.repr.TaskRepr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TaskService {

    private ToDoRepository toDoRepository;

    private UserService userService;

    @Autowired
    public TaskService(ToDoRepository toDoRepository, UserService userService) {
        this.toDoRepository = toDoRepository;
        this.userService = userService;
    }



    public Page<Task> findTasksByDescription(Long userId, String description, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return toDoRepository.findByUserIdAndDescriptionContainingIgnoreCase(userId, description, pageable);
    }

    public Page<Task> findToDosByUserIdAndKeyword(Long userId, String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return toDoRepository.findByUserIdAndKeyword(userId, keyword, pageable);
    }


    public Optional<TaskRepr> findById(Long id) {
        return toDoRepository.findById(id).map(TaskRepr::new);
    }

    public List<TaskRepr> findToDosByUserId(Long userId) {
        return toDoRepository.findToDosByUserId(userId);
    }

    public void save(TaskRepr taskRepr) {
        Optional<String> currentUserOptional = userService.getCurrentUser();
        if (currentUserOptional.isPresent()) {
            Optional<User> userOptional = userService.getByUsername(currentUserOptional.get());
            if (userOptional.isPresent()) {
                Task task = new Task();
                task.setId(taskRepr.getId());
                task.setDescription(taskRepr.getDescription());
                task.setTargetDate(taskRepr.getTargetDate());
                task.setUser(userOptional.get());
                toDoRepository.save(task);
            }
        }
    }

    public void delete(Long id) {
        toDoRepository.findById(id)
                .ifPresent(toDo -> toDoRepository.delete(toDo));
    }

}
