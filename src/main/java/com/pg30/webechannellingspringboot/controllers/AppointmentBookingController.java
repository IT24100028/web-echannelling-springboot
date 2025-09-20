package com.pg30.webechannellingspringboot.controllers;

import com.pg30.webechannellingspringboot.DTOs.DoctorDTO;
import com.pg30.webechannellingspringboot.DTOs.TimeSlotDTO;
import com.pg30.webechannellingspringboot.entities.BookingEntity;
import com.pg30.webechannellingspringboot.services.DBServices.DBService;
import org.springframework.beans.factory.annotation.Autowired;
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

        for (DoctorDTO doctor : doctors) {
            logger.info("Doctor Found: " + doctor.getFullName() + " | " +
                    doctor.getSpecialization() + " | " +
                    doctor.getEmail() + " | Fee: " + doctor.getFee());
        }

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





}
