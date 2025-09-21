package com.pg30.webechannellingspringboot.database.repositories;

import com.pg30.webechannellingspringboot.DTOs.MyBookingDTO;
import com.pg30.webechannellingspringboot.entities.BookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookingRepository extends JpaRepository<BookingEntity, Long> {

    @Query("SELECT new com.pg30.webechannellingspringboot.DTOs.MyBookingDTO(" +
            "b.bookingId, b.createdAt, CONCAT(u.firstName, ' ', u.lastName), " +
            "d.specialization, d.fee, t.slotDate, t.startTime) " +
            "FROM BookingEntity b " +
            "JOIN TimeSlotEntity t ON b.slotId = t.id " +
            "JOIN DoctorEntity d ON t.doctorId = d.doctorId " +
            "JOIN UserEntity u ON d.user.userId = u.userId " +
            "WHERE b.patientId = :patientId " +
            "ORDER BY b.createdAt DESC")
    List<MyBookingDTO> findBookingsByPatient(@Param("patientId") Long patientId);

}

