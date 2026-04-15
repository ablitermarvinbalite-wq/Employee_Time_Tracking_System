package com.example.timetracker.controller;

import com.example.timetracker.service.TimeRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
}