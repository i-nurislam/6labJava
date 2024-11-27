package com.example.todolist.controllers;

import com.example.todolist.exceptions.ResourceNotFoundException;
import com.example.todolist.persist.entity.Task;
import com.example.todolist.persist.entity.User;
import com.example.todolist.repr.TaskRepr;
import com.example.todolist.services.TaskService;
import com.example.todolist.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class TaskController {

    private TaskService todoService;

    private UserService userService;

    @Autowired
    public TaskController(TaskService taskService, UserService userService, TaskService taskService1) {
        this.todoService = taskService;
        this.userService = userService;
        this.taskService = taskService1;
    }

private final TaskService taskService;
public TaskController(TaskService taskService) {
    this.taskService = taskService;
}

@GetMapping("/")
public String indexPage(@RequestParam(value = "page", defaultValue = "0") int page,
                        @RequestParam(value = "size", defaultValue = "10") int size,
                        @RequestParam(value = "keyword", defaultValue = "") String keyword,
                        Model model) {
    String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();


    Optional<User> userOptional = userService.getByUsername(currentUsername);

    if (userOptional.isPresent()) {
        Long userId = userOptional.get().getId();


        Page<Task> todos = taskService.findToDosByUserIdAndKeyword(userId, keyword, page, size);

        model.addAttribute("todos", todos.getContent());
        model.addAttribute("totalPages", todos.getTotalPages());
        model.addAttribute("currentPage", page + 1);
        model.addAttribute("pageSize", size);
        model.addAttribute("keyword", keyword);
    }

    return "index";
}

    @GetMapping("/todo/{id}")
    public String todoPage(@PathVariable("id") Long id, Model model) {
        TaskRepr todoRepr = todoService.findById(id).orElseThrow(ResourceNotFoundException::new);
        model.addAttribute("todo", todoRepr);
        return "todo";
    }

    @GetMapping("/todo/create")
    public String createTodoPage(Model model) {
        model.addAttribute("todo", new TaskRepr());
        return "todo";
    }

    @PostMapping("/todo/create")
    public String createTodoPost(@ModelAttribute("todo") TaskRepr todo) {
        Optional<String> currentUserOptional = userService.getCurrentUser();
        if (currentUserOptional.isPresent()) {
            Optional<User> userOptional = userService.getByUsername(currentUserOptional.get());
            if (userOptional.isPresent()) {
                // Создание задачи для текущего пользователя
                todo.setUsername(userOptional.get().getUsername()); // Установить пользователя в todo
                todoService.save(todo); // Сохранение задачи
            }
        }
        return "redirect:/";
    }


    @GetMapping("/todo/delete/{id}")
    public String deleteTodo(@PathVariable Long id) {
        todoService.delete(id);
        return "redirect:/";
    }

    @GetMapping("/search")
    public String searchTasks(@RequestParam(value = "description", defaultValue = "") String description,
                              @RequestParam(value = "page", defaultValue = "0") int page,
                              @RequestParam(value = "size", defaultValue = "10") int size,
                              Model model) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> userOptional = userService.getByUsername(currentUsername);

        if (userOptional.isPresent()) {
            Long userId = userOptional.get().getId();
            Page<Task> tasks = taskService.findTasksByDescription(userId, description, page, size);

            model.addAttribute("todos", tasks.getContent());
            model.addAttribute("totalPages", tasks.getTotalPages());
            model.addAttribute("currentPage", page + 1);
            model.addAttribute("pageSize", size);
            model.addAttribute("description", description);
        }

        return "index";
    }


}
