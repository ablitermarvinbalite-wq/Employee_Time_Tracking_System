package com.example.timetracker.controller;

import com.example.timetracker.dto.RegisterRequest;
import com.example.timetracker.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import com.example.timetracker.dto.LoginRequest;
import com.example.timetracker.entity.User;

import com.example.timetracker.util.JwtUtil;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {
        userService.register(request.getUsername(), request.getPassword());
        return "User registered. Waiting for admin approval.";
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {

        User user = userService.login(
                request.getUsername(),
                request.getPassword()
        );

        return jwtUtil.generateToken(
                user.getUsername(),
                user.getRole().name()
        );
    }

}