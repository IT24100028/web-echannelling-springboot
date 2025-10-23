package com.pg30.webechannellingspringboot.database.repositories;

import com.pg30.webechannellingspringboot.entities.TimeSlotEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlotEntity, Long> {

    @Query(value = "SELECT ts.slot_id, ts.slot_date, ts.start_time, ts.doctor_id, " +
            "       COUNT(b.booking_id) AS booking_count " +
            "FROM dbo.time_slots ts " +
            "LEFT JOIN dbo.bookings b ON ts.slot_id = b.slot_id " +
            "WHERE ts.doctor_id = :doctorId " +
            "AND CAST(ts.slot_date AS DATETIME) + CAST(ts.start_time AS DATETIME) > :now " +
            "GROUP BY ts.slot_id, ts.slot_date, ts.start_time, ts.doctor_id " +
            "ORDER BY ts.slot_date ASC, ts.start_time ASC",
            nativeQuery = true)
    List<Object[]> findAvailableSlotsWithBookingCount(
            @Param("doctorId") Long doctorId,
            @Param("now") LocalDateTime now
    );

    // 1. Get all doctor slots, ordered by date+time
    @Query(value = "SELECT * FROM dbo.time_slots ts " +
            "WHERE ts.doctor_id = :doctorId " +
            "ORDER BY ts.slot_date ASC, ts.start_time ASC",
            nativeQuery = true)
    List<TimeSlotEntity> findByDoctorIdOrderByDateTime(@Param("doctorId") Long doctorId);

    // 2. Count overlaps safely (DATE + TIME → DATETIME)
    @Query(value = "SELECT COUNT(*) " +
            "FROM dbo.time_slots ts " +
            "WHERE ts.doctor_id = :doctorId " +
            "AND ts.slot_date = :date " +
            "AND (CAST(:startTime AS DATETIME) < CAST(ts.end_time AS DATETIME) " +
            "     AND CAST(:endTime AS DATETIME) > CAST(ts.start_time AS DATETIME))",
            nativeQuery = true)
    long countOverlaps(@Param("doctorId") Long doctorId,
                       @Param("date") LocalDate date,
                       @Param("startTime") LocalTime startTime,
                       @Param("endTime") LocalTime endTime);


}



