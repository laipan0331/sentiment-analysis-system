package com.lmz.sentiment_analysis.controller;

import com.lmz.sentiment_analysis.model.Comment;
import com.lmz.sentiment_analysis.repository.CommentRepository;
import com.lmz.sentiment_analysis.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final CommentRepository commentRepository;

    public AdminController(UserService userService, CommentRepository commentRepository) {
        this.userService = userService;
        this.commentRepository = commentRepository;
    }

    @GetMapping
    public String adminDashboard() {
        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("users", userService.findAllUsers());
        return "admin/users";
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/admin/users";
    }

    @GetMapping("/comments")
    public String listComments(Model model) {
        model.addAttribute("comments", commentRepository.findAll());
        return "admin/comments";
    }

    @GetMapping("/comments/delete/{id}")
    public String deleteComment(@PathVariable Long id) {
        commentRepository.deleteById(id);
        return "redirect:/admin/comments";
    }
}