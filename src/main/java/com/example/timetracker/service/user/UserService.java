package com.example.timetracker.service.user;

import com.example.timetracker.entity.*;
import com.example.timetracker.exception.user.UserNotFoundException;
import com.example.timetracker.exception.user.UserServiceException;
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
                    throw new UserServiceException("Username already exists");
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
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        user.setStatus(Status.APPROVED);
        userRepository.save(user);
    }

    public User authenticateUser(String username, String password) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserServiceException("Invalid username or password"));

        // 🔐 check password
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new UserServiceException("Invalid username or password");
        }

        // 🚫 check approval
        if (user.getStatus() != Status.APPROVED) {
            throw new UserServiceException("User not yet approved by admin");
        }

        return user;
    }

}