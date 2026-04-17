package com.example.timetracker.controller;

import com.example.timetracker.dto.DashboardResponse;
import com.example.timetracker.dto.TimeRecordResponse;
import com.example.timetracker.service.TimeRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/time")
@RequiredArgsConstructor
public class TimeRecordController {

    private final TimeRecordService timeRecordService;

    @PostMapping("/in/{userId}")
    public String timeIn(@PathVariable Long userId) {
        timeRecordService.timeIn(userId);
        return "Time in recorded";
    }

    @PostMapping("/out/{userId}")
    public String timeOut(@PathVariable Long userId) {
        timeRecordService.timeOut(userId);
        return "Time out recorded";
    }

    @GetMapping("/user/{userId}")
    public List<TimeRecordResponse> getUserRecords(
            @PathVariable Long userId
    ) {
        return timeRecordService.getUserRecords(userId);
    }

    @GetMapping("/user/{userId}/filter")
    public List<TimeRecordResponse> getUserRecordsByDate(
            @PathVariable Long userId,
            @RequestParam String start,
            @RequestParam String end
    ) {
        return timeRecordService.getUserRecordsByDate(
                userId,
                LocalDateTime.parse(start),
                LocalDateTime.parse(end)
        );
    }

    @GetMapping("/dashboard/{userId}")
    public DashboardResponse dashboard(@PathVariable Long userId) {
        return timeRecordService.getUserDashboard(userId);
    }

    @GetMapping("/dashboard/{userId}/monthly")
    public DashboardResponse monthly(
            @PathVariable Long userId,
            @RequestParam int year,
            @RequestParam int month
    ) {
        return timeRecordService.getMonthlyDashboard(userId, year, month);
    }

    @GetMapping("/export/{userId}")
    public String export(@PathVariable Long userId) {
        return timeRecordService.exportCsv(userId);
    }

}