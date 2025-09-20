package com.pg30.webechannellingspringboot.services.DBServices;

import com.pg30.webechannellingspringboot.DTOs.DoctorDTO;
import com.pg30.webechannellingspringboot.DTOs.TimeSlotDTO;
import com.pg30.webechannellingspringboot.database.repositories.BookingRepository;
import com.pg30.webechannellingspringboot.database.repositories.DoctorRepository;
import com.pg30.webechannellingspringboot.database.repositories.TimeSlotRepository;
import com.pg30.webechannellingspringboot.database.repositories.UserRepository;
import com.pg30.webechannellingspringboot.entities.BookingEntity;
import com.pg30.webechannellingspringboot.entities.DoctorEntity;
import com.pg30.webechannellingspringboot.entities.UserEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DBService {

    private final DoctorRepository doctorRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    public DBService(DoctorRepository doctorRepository, TimeSlotRepository timeSlotRepository, BookingRepository bookingRepository,UserRepository userRepository) {
        this.doctorRepository = doctorRepository;
        this.timeSlotRepository = timeSlotRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
    }

    public DoctorDTO getDoctorById(Long doctorId) {
        DoctorEntity doctor = doctorRepository.findById(doctorId).orElse(null);
        if (doctor == null) return null;

        return new DoctorDTO(
                doctor.getDoctorId(),
                doctor.getUser().getFirstName() + " " + doctor.getUser().getLastName(),
                doctor.getUser().getEmail(),
                doctor.getUser().getPhone(),
                doctor.getSpecialization(),
                doctor.getFee()
        );
    }

    public List<DoctorDTO> searchDoctors(String keyword) {
        List<DoctorEntity> doctors = doctorRepository.searchDoctors(keyword);
        return doctors.stream().map(d -> new DoctorDTO(
                d.getDoctorId(),
                d.getUser().getFirstName() + " " + d.getUser().getLastName(),
                d.getUser().getEmail(),
                d.getUser().getPhone(),
                d.getSpecialization(),
                d.getFee()
        )).collect(Collectors.toList());
    }

    public List<DoctorDTO> getAllDoctors() {
        List<DoctorEntity> doctors = doctorRepository.findAll();
        return doctors.stream().map(d -> new DoctorDTO(
                d.getDoctorId(),
                d.getUser().getFirstName() + " " + d.getUser().getLastName(),
                d.getUser().getEmail(),
                d.getUser().getPhone(),
                d.getSpecialization(),
                d.getFee()
        )).collect(Collectors.toList());
    }

    public List<TimeSlotDTO> getAvailableTimeSlots(Long doctorId) {
        LocalDateTime now = LocalDateTime.now();
        List<Object[]> results = timeSlotRepository.findAvailableSlotsWithBookingCount(doctorId, now);

        return results.stream().map(r -> new TimeSlotDTO(
                ((Number) r[0]).longValue(),
                ((java.sql.Date) r[1]).toLocalDate(),
                ((java.sql.Time) r[2]).toLocalTime(),
                ((Number) r[4]).longValue()
        )).collect(Collectors.toList());
    }

    public Integer getPatientIdByUsername(String username) {
        Optional<UserEntity> user = userRepository.findByEmail(username);
        if (user.isPresent()) {
            return user.get().getUserId();
        }
        return null;
    }


    public BookingEntity saveBooking(Long slotId, Long patientId) {
        BookingEntity booking = new BookingEntity();
        booking.setSlotId(slotId);
        booking.setPatientId(patientId);
        return bookingRepository.save(booking);
    }
}
