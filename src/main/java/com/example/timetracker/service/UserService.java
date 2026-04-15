package com.example.timetracker.service;

import com.example.timetracker.entity.*;
import com.example.timetracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public void register(String username, String password) {

        // check duplicate
        userRepository.findByUsername(username)
                .ifPresent(u -> {
                    throw new RuntimeException("Username already exists");
                });

        User user = new User();
        user.setUsername(username);
        user.setPassword(password); // plain for now (we fix later)
        user.setRole(Role.USER);
        user.setStatus(Status.PENDING);

        userRepository.save(user);
    }

    public void approveUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setStatus(Status.APPROVED);
        userRepository.save(user);
    }
}