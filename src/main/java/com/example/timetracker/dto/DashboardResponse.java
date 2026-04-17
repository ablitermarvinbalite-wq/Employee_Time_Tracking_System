package com.example.timetracker.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardResponse {

    private Long userId;
    private String username;

    private double totalHours;
    private double totalOvertime;
    private double totalBreakHours;

    private int workingDays;
}