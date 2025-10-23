package com.pg30.webechannellingspringboot.services.DBServices;

import com.pg30.webechannellingspringboot.DTOs.DoctorDTO;
import com.pg30.webechannellingspringboot.DTOs.MyBookingDTO;
import com.pg30.webechannellingspringboot.DTOs.TimeSlotDTO;
import com.pg30.webechannellingspringboot.database.repositories.BookingRepository;
import com.pg30.webechannellingspringboot.database.repositories.DoctorRepository;
import com.pg30.webechannellingspringboot.database.repositories.TimeSlotRepository;
import com.pg30.webechannellingspringboot.database.repositories.UserRepository;
import com.pg30.webechannellingspringboot.entities.BookingEntity;
import com.pg30.webechannellingspringboot.entities.DoctorEntity;
import com.pg30.webechannellingspringboot.entities.TimeSlotEntity;
import com.pg30.webechannellingspringboot.entities.UserEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DBService {

    private final DoctorRepository doctorRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    public DBService(DoctorRepository doctorRepository, TimeSlotRepository timeSlotRepository,
                     BookingRepository bookingRepository, UserRepository userRepository) {
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
    public List<TimeSlotDTO> getDoctorTimeSlots(Long doctorId) {
        return timeSlotRepository.findByDoctorIdOrderByDateTime(doctorId)
                .stream()
                .map(ts -> {
                    Long bookings = bookingRepository.countBySlotId(ts.getId());
                    return new TimeSlotDTO(
                            ts.getId(),
                            ts.getSlotDate(),
                            ts.getStartTime(),
                            bookings
                    );
                })
                .collect(Collectors.toList());
    }



    public Long getDoctorIdByUsername(String username) {
        Optional<UserEntity> user = userRepository.findByEmail(username);
        if (user.isEmpty()) return null;
        DoctorEntity doctor = doctorRepository.findByUser_UserId(user.get().getUserId().longValue());
        return doctor != null ? doctor.getDoctorId() : null;
    }

    public String getUserRoleByUsername(String username) {
        Optional<UserEntity> user = userRepository.findByEmail(username);
        return user.map(UserEntity::getRole).orElse(null);
    }

    // User management methods
    public boolean userExistsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public boolean userExistsByPhone(String phone) {
        return userRepository.findByPhone(phone).isPresent();
    }

    public UserEntity saveUser(UserEntity user) {
        return userRepository.save(user);
    }

    public DoctorEntity saveDoctor(DoctorEntity doctor) {
        return doctorRepository.save(doctor);
    }

    public DoctorEntity getDoctorEntityById(Long doctorId) {
        return doctorRepository.findById(doctorId).orElse(null);
    }

    public boolean updateDoctorProfile(Long doctorId, String firstName, String lastName, String phone, String specialization, BigDecimal fee) {
        try {
            DoctorEntity doctor = doctorRepository.findById(doctorId).orElse(null);
            if (doctor == null) return false;

            UserEntity user = doctor.getUser();
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setPhone(phone);
            userRepository.save(user);

            doctor.setSpecialization(specialization);
            doctor.setFee(fee);
            doctorRepository.save(doctor);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean deleteDoctorAccount(Long doctorId) {
        try {
            DoctorEntity doctor = doctorRepository.findById(doctorId).orElse(null);
            if (doctor == null) return false;

            UserEntity user = doctor.getUser();

            // Delete doctor first (due to foreign key constraint)
            doctorRepository.delete(doctor);
            // Then delete user
            userRepository.delete(user);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Ensures there is a Doctor row for the given username. If missing, creates a minimal doctor
     * profile with default specialization and zero fee so the user can manage time slots.
     */
    public Long ensureDoctorIdByUsername(String username) {
        if (username == null) return null;
        Optional<UserEntity> userOpt = userRepository.findByEmail(username);
        if (userOpt.isEmpty()) return null;
        UserEntity user = userOpt.get();
        DoctorEntity doctor = doctorRepository.findByUser_UserId(user.getUserId().longValue());
        if (doctor == null) {
            DoctorEntity created = new DoctorEntity();
            created.setUser(user);
            created.setSpecialization("General");
            created.setFee(BigDecimal.ZERO);
            doctor = doctorRepository.save(created);
        }
        return doctor.getDoctorId();
    }

    public String validateNoOverlap(Long doctorId, java.time.LocalDate date, java.time.LocalTime startTime, java.time.LocalTime endTime) {
        if (endTime.isBefore(startTime) || endTime.equals(startTime)) {
            return "Invalid time range";
        }
        long overlaps = timeSlotRepository.countOverlaps(doctorId, date, startTime, endTime);
        if (overlaps > 0) {
            return "Time overlaps with existing schedule";
        }
        return null;
    }

    public TimeSlotEntity createTimeSlot(Long doctorId, java.time.LocalDate date, java.time.LocalTime startTime, java.time.LocalTime endTime, Integer maxPatients) {
        if (doctorId == null) {
            throw new IllegalArgumentException("Doctor not found for current user");
        }
        String error = validateNoOverlap(doctorId, date, startTime, endTime);
        if (error != null) {
            throw new IllegalArgumentException(error);
        }
        TimeSlotEntity ts = new TimeSlotEntity();
        ts.setDoctorId(doctorId);
        ts.setSlotDate(date);
        ts.setStartTime(startTime);
        ts.setEndTime(endTime);
        ts.setCreatedAt(LocalDateTime.now());
        return timeSlotRepository.save(ts);
    }

    public TimeSlotEntity updateTimeSlot(Long slotId, Long doctorId, java.time.LocalDate date, java.time.LocalTime startTime, java.time.LocalTime endTime, Integer maxPatients) {
        TimeSlotEntity existing = timeSlotRepository.findById(slotId).orElseThrow(() -> new IllegalArgumentException("Slot not found"));
        if (!existing.getDoctorId().equals(doctorId)) {
            throw new IllegalArgumentException("Unauthorized update");
        }
        // exclude current slot from overlap: temporarily remove by adjusting check if times changed
        String error = null;
        if (!existing.getSlotDate().equals(date) || !existing.getStartTime().equals(startTime) || !existing.getEndTime().equals(endTime)) {
            error = validateNoOverlap(doctorId, date, startTime, endTime);
        }
        if (error != null) {
            throw new IllegalArgumentException(error);
        }
        existing.setSlotDate(date);
        existing.setStartTime(startTime);
        existing.setEndTime(endTime);
        return timeSlotRepository.save(existing);
    }

    public void deleteTimeSlot(Long slotId, Long doctorId) {
        TimeSlotEntity existing = timeSlotRepository.findById(slotId).orElseThrow(() -> new IllegalArgumentException("Slot not found"));
        if (!existing.getDoctorId().equals(doctorId)) {
            throw new IllegalArgumentException("Unauthorized delete");
        }
        timeSlotRepository.deleteById(slotId);
    }

    public Integer getPatientIdByUsername(String username) {
        Optional<UserEntity> user = userRepository.findByEmail(username);
        return user.map(UserEntity::getUserId).orElse(null);
    }

    public BookingEntity saveBooking(Long slotId, Long patientId) {
        BookingEntity booking = new BookingEntity();
        booking.setSlotId(slotId);
        booking.setPatientId(patientId);
        return bookingRepository.save(booking);
    }


    public List<MyBookingDTO> getBookingsByPatientId(Long patientId) {
        return bookingRepository.findBookingsByPatient(patientId);
    }

    public boolean cancelBooking(Long bookingId) {
        Optional<BookingEntity> bookingOpt = bookingRepository.findById(bookingId);
        if (bookingOpt.isPresent()) {
            bookingRepository.deleteById(bookingId);
            return true;
        }
        return false;
    }
}
