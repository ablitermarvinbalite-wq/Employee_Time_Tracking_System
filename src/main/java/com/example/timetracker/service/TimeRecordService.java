package com.example.timetracker.service;

import com.example.timetracker.dto.TimeRecordResponse;
import com.example.timetracker.entity.TimeRecord;
import com.example.timetracker.entity.User;
import com.example.timetracker.repository.TimeRecordRepository;
import com.example.timetracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.Optional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TimeRecordService {

    private final TimeRecordRepository timeRecordRepository;
    private final UserRepository userRepository;

    // ⏱️ TIME IN
    public void timeIn(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // prevent multiple active sessions
        Optional<TimeRecord> active = timeRecordRepository.findByUserAndTimeOutIsNull(user);
        if (active.isPresent()) {
            throw new RuntimeException("Already timed in");
        }

        // prevent multiple time-in per day
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.atTime(LocalTime.MAX);

        timeRecordRepository
                .findFirstByUserAndTimeInBetween(user, start, end)
                .ifPresent(r -> {
                    throw new RuntimeException("Already timed in today");
                });

        TimeRecord record = new TimeRecord();
        record.setUser(user);
        record.setTimeIn(LocalDateTime.now());

        timeRecordRepository.save(record);
    }

    // ⏱️ TIME OUT + COMPUTE
    public void timeOut(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        TimeRecord record = timeRecordRepository
                .findByUserAndTimeOutIsNull(user)
                .orElseThrow(() -> new RuntimeException("No active time-in"));

        LocalDateTime timeOut = LocalDateTime.now();
        record.setTimeOut(timeOut);

        // 🧮 COMPUTATION
        double totalHours = Duration.between(
                record.getTimeIn(), timeOut
        ).toMinutes() / 60.0;

        double breakHours = 1.0;

        double overtime = 0.0;

        if (totalHours > 9) {
            overtime = totalHours - 9;

            // every 9 hours OT → deduct 1 hour
            double deduction = Math.floor(overtime / 9);
            overtime -= deduction;
        }

        record.setTotalHours(totalHours);
        record.setBreakHours(breakHours);
        record.setOvertimeHours(overtime);

        timeRecordRepository.save(record);
    }

    public List<TimeRecordResponse> getUserRecords(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return timeRecordRepository.findByUser(user)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<TimeRecordResponse> getUserRecordsByDate(
            Long userId,
            LocalDateTime start,
            LocalDateTime end
    ) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return timeRecordRepository
                .findByUserAndTimeInBetween(user, start, end)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<TimeRecordResponse> getAllRecords(
            LocalDateTime start,
            LocalDateTime end
    ) {

        return timeRecordRepository
                .findByTimeInBetween(start, end)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private TimeRecordResponse mapToResponse(TimeRecord record) {
        return TimeRecordResponse.builder()
                .id(record.getId())
                .userId(record.getUser().getId())
                .username(record.getUser().getUsername())
                .timeIn(record.getTimeIn())
                .timeOut(record.getTimeOut())
                .totalHours(record.getTotalHours())
                .breakHours(record.getBreakHours())
                .overtimeHours(record.getOvertimeHours())
                .build();
    }

}