package com.example.timetracker.controller;

import com.example.timetracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    @PutMapping("/approve/{userId}")
    public String approve(@PathVariable Long userId) {
        userService.approveUser(userId);
        return "User approved";
    }
}