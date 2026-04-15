package com.example.timetracker.controller;

import com.example.timetracker.dto.RegisterRequest;
import com.example.timetracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {
        userService.register(request.getUsername(), request.getPassword());
        return "User registered. Waiting for admin approval.";
    }
}