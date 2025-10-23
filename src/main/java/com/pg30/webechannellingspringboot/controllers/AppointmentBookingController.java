package com.pg30.webechannellingspringboot.controllers;

import com.pg30.webechannellingspringboot.DTOs.DoctorDTO;
import com.pg30.webechannellingspringboot.DTOs.MyBookingDTO;
import com.pg30.webechannellingspringboot.DTOs.TimeSlotDTO;
import com.pg30.webechannellingspringboot.entities.BookingEntity;
import com.pg30.webechannellingspringboot.services.DBServices.DBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Controller
public class AppointmentBookingController {

    Logger logger = Logger.getLogger(AppointmentBookingController.class.getName());

    @Autowired
    private DBService dbService;

    @RequestMapping(value = "/appointments", method = RequestMethod.GET)
    public String showAppointmentsPage(@RequestParam(value = "query", required = false) String query, Model model) {
        List<DoctorDTO> doctors;
        if (query == null || query.isEmpty()) {
            logger.info("No search query provided. Returning all doctors.");
            doctors = dbService.getAllDoctors();
        } else {
            logger.info("Searching doctors with query: " + query);
            doctors = dbService.searchDoctors(query);
        }

//        for (DoctorDTO doctor : doctors) {
//            logger.info("Doctor Found: " + doctor.getFullName() + " | " +
//                    doctor.getSpecialization() + " | " +
//                    doctor.getEmail() + " | Fee: " + doctor.getFee());
//        }

        model.addAttribute("doctors", doctors);
        return "appointment";
    }

    @RequestMapping(value = "/timeslots/{id}", method = RequestMethod.GET)
    public String showTimeSlots(@PathVariable("id") Long doctorId, Model model) {
        logger.info("Requested timeslots for doctor ID: " + doctorId);
        List<TimeSlotDTO> availableSlots = dbService.getAvailableTimeSlots(doctorId);
        DoctorDTO doctorDTO = dbService.getDoctorById(doctorId);
        model.addAttribute("timeSlots", availableSlots);
        model.addAttribute("doctor", doctorDTO);
        return "time-slots";
    }

    @RequestMapping(value = "/book", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> bookSlot(@RequestParam("slotId") Long slotId) {
        Map<String, Object> response = new HashMap<>();
        logger.info("Received booking request for Slot ID: " + slotId);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication != null ? authentication.getName() : null;
        logger.info("Authenticated username: " + username);

        if (username == null) {
            response.put("success", false);
            response.put("message", "You must be logged in to book a slot.");
            return response;
        }

        Integer patientId = dbService.getPatientIdByUsername(username);
        logger.info("Resolved patient ID: " + patientId + " for username: " + username);

        if (patientId == null) {
            response.put("success", false);
            response.put("message", "Patient record not found.");
            return response;
        }

        BookingEntity booking = dbService.saveBooking(slotId, Long.valueOf(patientId));
        logger.info("Booking saved successfully. Booking ID: " + booking.getBookingId() +
                ", Slot ID: " + slotId + ", Patient ID: " + patientId);

        response.put("success", true);
        response.put("message", "Slot booked successfully!");
        response.put("bookingId", booking.getBookingId());

        return response;
    }


    @RequestMapping(value = "/my-bookings", method = RequestMethod.GET)
    public String myBookings(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication != null ? authentication.getName() : null;

        logger.info("Loading bookings for user: " + username);

        Integer patientId = dbService.getPatientIdByUsername(username);

        List<MyBookingDTO> bookings = dbService.getBookingsByPatientId(patientId.longValue());

//        bookings.forEach(booking -> {
//            logger.info("Booking ID: " + booking.getBookingId());
//            logger.info("Doctor: " + booking.getDoctorName());
//            logger.info("Specialization: " + booking.getSpecialization());
//            logger.info("Fee: " + booking.getFee());
//            logger.info("Slot Date: " + booking.getSlotDate());
//            logger.info("Start Time: " + booking.getStartTime());
//            logger.info("Created At: " + booking.getCreatedAt());
//            logger.info("-------------------------------");
//        });

        model.addAttribute("bookings", bookings);

        return "my-bookings";
    }

    @RequestMapping(value = "/cancel-booking", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> cancelBooking(@RequestParam Long bookingId) {
        Map<String, Object> response = new HashMap<>();
        boolean success = dbService.cancelBooking(bookingId);
        if (success) {
            response.put("success", true);
            response.put("message", "Booking cancelled successfully.");
        } else {
            response.put("success", false);
            response.put("message", "Booking not found or could not be cancelled.");
        }
        return ResponseEntity.ok(response);
    }
}
