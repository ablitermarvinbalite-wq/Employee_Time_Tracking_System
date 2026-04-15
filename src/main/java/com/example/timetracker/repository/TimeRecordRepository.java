package com.example.timetracker.repository;

import com.example.timetracker.entity.TimeRecord;
import com.example.timetracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TimeRecordRepository extends JpaRepository<TimeRecord, Long> {

    // find active session (no timeOut yet)
    Optional<TimeRecord> findByUserAndTimeOutIsNull(User user);

    // optional: prevent multiple time-in per day
    Optional<TimeRecord> findByUserAndTimeInBetween(
            User user,
            LocalDateTime start,
            LocalDateTime end
    );
}