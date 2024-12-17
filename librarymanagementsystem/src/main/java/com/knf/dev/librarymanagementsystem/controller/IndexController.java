package com.knf.dev.librarymanagementsystem.controller;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.knf.dev.librarymanagementsystem.entity.Role;
import com.knf.dev.librarymanagementsystem.entity.User;
import com.knf.dev.librarymanagementsystem.repository.UserRepository;

@Controller
public class IndexController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(
        @RequestParam("first_name") String firstName,
        @RequestParam("last_name") String lastName,
        User user, Model model) {

        user.setFirstName(firstName);
        user.setLastName(lastName);

        // Kiểm tra và lưu user
        if (userRepository.findByEmail(user.getEmail()) != null) {
            model.addAttribute("error", "Email đã được sử dụng!");
            return "register";
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Arrays.asList(new Role("ROLE_USER")));
        userRepository.save(user);

        model.addAttribute("success", "Đăng ký thành công!");
        return "redirect:/login"; 
    }

    @GetMapping("/login-success")
    public String loginSuccess() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = "";

        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        User user = userRepository.findByEmail(username);
        if (user != null && user.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_ADMIN"))) {
            return "redirect:/books";
        } else {
            return "redirect:/reads"; 
        }
    }
}
