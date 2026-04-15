package com.example.timetracker.controller;

import com.example.timetracker.dto.TimeRecordResponse;
import com.example.timetracker.service.TimeRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin/records")
@RequiredArgsConstructor
public class AdminRecordController {

    private final TimeRecordService timeRecordService;

    @GetMapping
    public List<TimeRecordResponse> getAll(
            @RequestParam String start,
            @RequestParam String end
    ) {
        return timeRecordService.getAllRecords(
                LocalDateTime.parse(start),
                LocalDateTime.parse(end)
        );
    }
}