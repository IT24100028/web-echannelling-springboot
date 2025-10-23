package com.pg30.webechannellingspringboot.controllers;

import com.pg30.webechannellingspringboot.DTOs.TimeSlotDTO;
import com.pg30.webechannellingspringboot.entities.TimeSlotEntity;
import com.pg30.webechannellingspringboot.services.DBServices.DBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class DoctorTimeSlotController {

    @Autowired
    private DBService dbService;

    @GetMapping("/doctor/slots")
    public String slotsPage(Model model) {
        return "doctor-time-slots";
    }

    @GetMapping("/api/doctor/slots")
    @ResponseBody
    public List<TimeSlotDTO> listSlots() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication != null ? authentication.getName() : null;
        Long doctorId = dbService.getDoctorIdByUsername(username);
        return dbService.getDoctorTimeSlots(doctorId);
    }

    @PostMapping("/api/doctor/slots")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createSlot(@RequestParam("date") String date,
                                                          @RequestParam("startTime") String start,
                                                          @RequestParam("endTime") String end,
                                                          @RequestParam("maxPatients") Integer maxPatients) {
        Map<String, Object> res = new HashMap<>();
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication != null ? authentication.getName() : null;
            
            if (username == null) {
                res.put("success", false);
                res.put("message", "Authentication required");
                return ResponseEntity.status(401).body(res);
            }
            
            String userRole = dbService.getUserRoleByUsername(username);
            if (userRole == null || (!userRole.equals("DOCTOR") && !userRole.equals("ADMIN"))) {
                res.put("success", false);
                res.put("message", "Access denied. Only doctors and admins can create time slots.");
                return ResponseEntity.status(403).body(res);
            }
            
            Long doctorId = dbService.getDoctorIdByUsername(username);
            if (doctorId == null) {
                res.put("success", false);
                res.put("message", "Doctor profile not found. Please contact administrator.");
                return ResponseEntity.badRequest().body(res);
            }
            
            TimeSlotEntity ts = dbService.createTimeSlot(doctorId,
                    LocalDate.parse(date), LocalTime.parse(start), LocalTime.parse(end), maxPatients);
            res.put("success", true);
            res.put("id", ts.getId());
            return ResponseEntity.ok(res);
        } catch (IllegalArgumentException ex) {
            res.put("success", false);
            res.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(res);
        }
    }

    @PutMapping("/api/doctor/slots/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateSlot(@PathVariable("id") Long id,
                                                          @RequestParam("date") String date,
                                                          @RequestParam("startTime") String start,
                                                          @RequestParam("endTime") String end,
                                                          @RequestParam("maxPatients") Integer maxPatients) {
        Map<String, Object> res = new HashMap<>();
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication != null ? authentication.getName() : null;
            
            if (username == null) {
                res.put("success", false);
                res.put("message", "Authentication required");
                return ResponseEntity.status(401).body(res);
            }
            
            String userRole = dbService.getUserRoleByUsername(username);
            if (userRole == null || (!userRole.equals("DOCTOR") && !userRole.equals("ADMIN"))) {
                res.put("success", false);
                res.put("message", "Access denied. Only doctors and admins can update time slots.");
                return ResponseEntity.status(403).body(res);
            }
            
            Long doctorId = dbService.getDoctorIdByUsername(username);
            if (doctorId == null) {
                res.put("success", false);
                res.put("message", "Doctor profile not found. Please contact administrator.");
                return ResponseEntity.badRequest().body(res);
            }
            
            dbService.updateTimeSlot(id, doctorId,
                    LocalDate.parse(date), LocalTime.parse(start), LocalTime.parse(end), maxPatients);
            res.put("success", true);
            return ResponseEntity.ok(res);
        } catch (IllegalArgumentException ex) {
            res.put("success", false);
            res.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(res);
        }
    }

    @DeleteMapping("/api/doctor/slots/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteSlot(@PathVariable("id") Long id) {
        Map<String, Object> res = new HashMap<>();
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication != null ? authentication.getName() : null;
            
            if (username == null) {
                res.put("success", false);
                res.put("message", "Authentication required");
                return ResponseEntity.status(401).body(res);
            }
            
            String userRole = dbService.getUserRoleByUsername(username);
            if (userRole == null || (!userRole.equals("DOCTOR") && !userRole.equals("ADMIN"))) {
                res.put("success", false);
                res.put("message", "Access denied. Only doctors and admins can delete time slots.");
                return ResponseEntity.status(403).body(res);
            }
            
            Long doctorId = dbService.getDoctorIdByUsername(username);
            if (doctorId == null) {
                res.put("success", false);
                res.put("message", "Doctor profile not found. Please contact administrator.");
                return ResponseEntity.badRequest().body(res);
            }
            
            dbService.deleteTimeSlot(id, doctorId);
            res.put("success", true);
            return ResponseEntity.ok(res);
        } catch (IllegalArgumentException ex) {
            res.put("success", false);
            res.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(res);
        }
    }
}


