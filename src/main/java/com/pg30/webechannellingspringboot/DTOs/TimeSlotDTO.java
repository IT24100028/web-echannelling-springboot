package com.pg30.webechannellingspringboot.DTOs;

import java.time.LocalDate;
import java.time.LocalTime;

public class TimeSlotDTO {
    private Long id;
    private LocalDate slotDate;
    private LocalTime startTime;
    private Long bookingCount; // new field

    public TimeSlotDTO(Long id, LocalDate slotDate, LocalTime startTime) {
        this.id = id;
        this.slotDate = slotDate;
        this.startTime = startTime;
        this.bookingCount = 0L; // default 0
    }

    // new constructor with booking count
    public TimeSlotDTO(Long id, LocalDate slotDate, LocalTime startTime, Long bookingCount) {
        this.id = id;
        this.slotDate = slotDate;
        this.startTime = startTime;
        this.bookingCount = bookingCount;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getSlotDate() { return slotDate; }
    public void setSlotDate(LocalDate slotDate) { this.slotDate = slotDate; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public Long getBookingCount() { return bookingCount; }
    public void setBookingCount(Long bookingCount) { this.bookingCount = bookingCount; }
}
