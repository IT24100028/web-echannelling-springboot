package com.pg30.webechannellingspringboot.DTOs;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

public class MyBookingDTO {
    private Long bookingId;
    private LocalDateTime createdAt;
    private String doctorName;
    private String specialization;
    private BigDecimal fee;
    private LocalDate slotDate;
    private LocalTime startTime;

    public MyBookingDTO(Long bookingId, LocalDateTime createdAt, String doctorName,
                        String specialization, BigDecimal fee,
                        LocalDate slotDate, LocalTime startTime) {
        this.bookingId = bookingId;
        this.createdAt = createdAt;
        this.doctorName = doctorName;
        this.specialization = specialization;
        this.fee = fee;
        this.slotDate = slotDate;
        this.startTime = startTime;
    }

    public Long getBookingId() { return bookingId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public String getDoctorName() { return doctorName; }
    public String getSpecialization() { return specialization; }
    public BigDecimal getFee() { return fee; }
    public LocalDate getSlotDate() { return slotDate; }
    public LocalTime getStartTime() { return startTime; }
}
