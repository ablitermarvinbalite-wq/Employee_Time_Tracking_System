package com.example.timetracker.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TimeRecordResponse {

    private Long id;
    private Long userId;
    private String username;

    private LocalDateTime timeIn;
    private LocalDateTime timeOut;

    private Double totalHours;
    private Double breakHours;
    private Double overtimeHours;
}