package com.lmz.sentiment_analysis.controller;

import com.lmz.sentiment_analysis.model.User;
import com.lmz.sentiment_analysis.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
//This controller manages authentication-related endpoints for the application.
// It provides methods to display registration and login forms, handle user registration
// It defines the home page for authenticated users.
public class AuthController {

    @Autowired
    private UserService userService;

    //Displays the registration form.
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    //Processes the user registration form submission.
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user,
                               BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "register";
        }
        try {

            userService.registerNewUser(user);
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
        return "redirect:/login?registered";
    }

    //Displays the login form.
    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    //Displays the home page for authenticated users.
    @GetMapping("/")
    public String home() {
        return "index";
    }
}
