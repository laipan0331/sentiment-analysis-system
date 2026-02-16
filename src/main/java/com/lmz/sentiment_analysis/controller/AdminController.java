package com.lmz.sentiment_analysis.controller;

import com.lmz.sentiment_analysis.repository.CommentRepository;
import com.lmz.sentiment_analysis.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
// This controller manages administrative operations including user and comment management.
// It provides endpoints for viewing the admin dashboard, listing users, deleting a user,
// listing comments, and deleting a comment.
public class AdminController {
    private final UserService userService;
    private final CommentRepository commentRepository;

    // Constructor injection for UserService and CommentRepository dependencies.
    public AdminController(UserService userService, CommentRepository commentRepository) {
        this.userService = userService;
        this.commentRepository = commentRepository;
    }

    //Handles GET requests to the admin dashboard.
    @GetMapping
    public String adminDashboard() {
        return "admin/dashboard";
    }

    //Handles GET requests to list all users.
    //Retrieves a list of users from the UserService and adds it to the model.
    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("users", userService.findAllUsers());
        return "admin/users";
    }

    //Handles GET requests to delete a user by id.
    //After deletion, redirects back to the user list page.
    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/admin/users";
    }

    //Handles GET requests to list all comments.
    //Retrieves a list of comments from the CommentRepository and adds it to the model.
    @GetMapping("/comments")
    public String listComments(Model model) {
        model.addAttribute("comments", commentRepository.findAll());
        return "admin/comments";
    }

    //Handles GET requests to delete a comment by id.
    //After deletion, redirects back to the comments list page.
    @GetMapping("/comments/delete/{id}")
    public String deleteComment(@PathVariable Long id) {
        commentRepository.deleteById(id);
        return "redirect:/admin/comments";
    }
}
