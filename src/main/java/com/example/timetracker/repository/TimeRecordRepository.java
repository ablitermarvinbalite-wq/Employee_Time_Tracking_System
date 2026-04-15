package com.example.timetracker.repository;

import com.example.timetracker.entity.TimeRecord;
import com.example.timetracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TimeRecordRepository extends JpaRepository<TimeRecord, Long> {

    // find active session (no timeOut yet)
    Optional<TimeRecord> findByUserAndTimeOutIsNull(User user);

    List<TimeRecord> findByUser(User user);

    List<TimeRecord> findByUserAndTimeInBetween(
            User user,
            LocalDateTime start,
            LocalDateTime end
    );

    List<TimeRecord> findByTimeInBetween(
            LocalDateTime start,
            LocalDateTime end
    );

    Optional<TimeRecord> findFirstByUserAndTimeInBetween(
            User user,
            LocalDateTime start,
            LocalDateTime end
    );

}