package com.example.timetracker.service.user;

import com.example.timetracker.entity.*;
import com.example.timetracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void register(String username, String password) {

        userRepository.findByUsername(username)
                .ifPresent(u -> {
                    throw new RuntimeException("Username already exists");
                });

        User user = new User();
        user.setUsername(username);

        // 🔐 ENCRYPT PASSWORD
        user.setPassword(passwordEncoder.encode(password));

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

    public User login(String username, String password) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        // 🔐 check password
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        // 🚫 check approval
        if (user.getStatus() != Status.APPROVED) {
            throw new RuntimeException("User not yet approved by admin");
        }

        return user;
    }

}