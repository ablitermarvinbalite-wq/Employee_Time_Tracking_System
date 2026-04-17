package com.example.timetracker.service;

import com.example.timetracker.dto.DashboardResponse;
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

    public DashboardResponse getUserDashboard(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<TimeRecord> records = timeRecordRepository.findByUser(user);

        double totalHours = 0;
        double totalOvertime = 0;
        double totalBreak = 0;

        int workingDays = 0;

        for (TimeRecord r : records) {
            if (r.getTimeOut() != null) {
                totalHours += safe(r.getTotalHours());
                totalOvertime += safe(r.getOvertimeHours());
                totalBreak += safe(r.getBreakHours());
                workingDays++;
            }
        }

        return DashboardResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .totalHours(totalHours)
                .totalOvertime(totalOvertime)
                .totalBreakHours(totalBreak)
                .workingDays(workingDays)
                .build();
    }

    private double safe(Double val) {
        return val == null ? 0.0 : val;
    }

    public DashboardResponse getMonthlyDashboard(Long userId, int year, int month) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        LocalDateTime start = LocalDate.of(year, month, 1).atStartOfDay();
        LocalDateTime end = start.plusMonths(1).minusSeconds(1);

        List<TimeRecord> records =
                timeRecordRepository.findByUserAndTimeInBetween(user, start, end);

        double totalHours = 0;
        double totalOvertime = 0;
        double totalBreak = 0;
        int workingDays = 0;

        for (TimeRecord r : records) {
            if (r.getTimeOut() != null) {
                totalHours += safe(r.getTotalHours());
                totalOvertime += safe(r.getOvertimeHours());
                totalBreak += safe(r.getBreakHours());
                workingDays++;
            }
        }

        return DashboardResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .totalHours(totalHours)
                .totalOvertime(totalOvertime)
                .totalBreakHours(totalBreak)
                .workingDays(workingDays)
                .build();
    }

    public String exportCsv(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<TimeRecord> records = timeRecordRepository.findByUser(user);

        StringBuilder sb = new StringBuilder();

        sb.append("Date,Time In,Time Out,Total Hours,Break,Overtime\n");

        for (TimeRecord r : records) {
            sb.append(r.getTimeIn()).append(",")
                    .append(r.getTimeOut()).append(",")
                    .append(r.getTotalHours()).append(",")
                    .append(r.getBreakHours()).append(",")
                    .append(r.getOvertimeHours())
                    .append("\n");
        }

        return sb.toString();
    }

}