package com.pg30.webechannellingspringboot.controllers;

import com.pg30.webechannellingspringboot.entities.DoctorEntity;
import com.pg30.webechannellingspringboot.entities.UserEntity;
import com.pg30.webechannellingspringboot.services.DBServices.DBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/doctor")
public class DoctorController {

    @Autowired
    private DBService dbService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;


    @GetMapping("/signup")
    public String doctorSignupPage() {
        return "doctor-signup";
    }


    @GetMapping("/login")
    public String doctorLoginPage() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() &&
                authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_DOCTOR") || a.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/doctor/dashboard";
        }
        return "doctor-login";
    }

    @GetMapping("/dashboard")
    public String doctorDashboard(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication != null ? authentication.getName() : null;
        
        if (username == null) {
            return "redirect:/doctor/login";
        }

        Long doctorId = dbService.getDoctorIdByUsername(username);
        if (doctorId == null) {
            return "redirect:/doctor/login";
        }

        DoctorEntity doctor = dbService.getDoctorEntityById(doctorId);
        if (doctor != null) {
            model.addAttribute("doctor", doctor);
        }

        return "doctor-dashboard";
    }

    @PostMapping("/signup")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> doctorSignup(@RequestParam("firstName") String firstName,
                                                           @RequestParam("lastName") String lastName,
                                                           @RequestParam("email") String email,
                                                           @RequestParam("phone") String phone,
                                                           @RequestParam("password") String password,
                                                           @RequestParam("specialty") String specialty,
                                                           @RequestParam("fee") BigDecimal fee) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (dbService.userExistsByEmail(email)) {
                response.put("success", false);
                response.put("message", "Email already registered. Please use a different email.");
                return ResponseEntity.badRequest().body(response);
            }

            if (dbService.userExistsByPhone(phone)) {
                response.put("success", false);
                response.put("message", "Phone number already registered. Please use a different phone number.");
                return ResponseEntity.badRequest().body(response);
            }

            UserEntity user = new UserEntity();
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmail(email);
            user.setPhone(phone);
            user.setPassword(passwordEncoder.encode(password));
            user.setRole("DOCTOR");

            UserEntity savedUser = dbService.saveUser(user);

            DoctorEntity doctor = new DoctorEntity();
            doctor.setUser(savedUser);
            doctor.setSpecialization(specialty);
            doctor.setFee(fee != null ? fee : BigDecimal.ZERO);

            DoctorEntity savedDoctor = dbService.saveDoctor(doctor);

            response.put("success", true);
            response.put("message", "Doctor account created successfully!");
            response.put("doctorId", savedDoctor.getDoctorId());
            response.put("userId", savedUser.getUserId());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error creating doctor account: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/api/profile")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getDoctorProfile() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication != null ? authentication.getName() : null;
            
            if (username == null) {
                response.put("success", false);
                response.put("message", "Authentication required");
                return ResponseEntity.status(401).body(response);
            }

            Long doctorId = dbService.getDoctorIdByUsername(username);
            if (doctorId == null) {
                response.put("success", false);
                response.put("message", "Doctor profile not found");
                return ResponseEntity.badRequest().body(response);
            }

            DoctorEntity doctor = dbService.getDoctorEntityById(doctorId);
            if (doctor == null) {
                response.put("success", false);
                response.put("message", "Doctor profile not found");
                return ResponseEntity.badRequest().body(response);
            }

            Map<String, Object> doctorData = new HashMap<>();
            doctorData.put("doctorId", doctor.getDoctorId());
            doctorData.put("firstName", doctor.getUser().getFirstName());
            doctorData.put("lastName", doctor.getUser().getLastName());
            doctorData.put("email", doctor.getUser().getEmail());
            doctorData.put("phone", doctor.getUser().getPhone());
            doctorData.put("specialization", doctor.getSpecialization());
            doctorData.put("fee", doctor.getFee());

            response.put("success", true);
            response.put("doctor", doctorData);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error retrieving doctor profile: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/api/profile")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateDoctorProfile(@RequestParam("firstName") String firstName,
                                                                  @RequestParam("lastName") String lastName,
                                                                  @RequestParam("phone") String phone,
                                                                  @RequestParam("specialization") String specialization,
                                                                  @RequestParam("fee") BigDecimal fee) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication != null ? authentication.getName() : null;
            
            if (username == null) {
                response.put("success", false);
                response.put("message", "Authentication required");
                return ResponseEntity.status(401).body(response);
            }

            Long doctorId = dbService.getDoctorIdByUsername(username);
            if (doctorId == null) {
                response.put("success", false);
                response.put("message", "Doctor profile not found");
                return ResponseEntity.badRequest().body(response);
            }

            boolean updated = dbService.updateDoctorProfile(doctorId, firstName, lastName, phone, specialization, fee);
            
            if (updated) {
                response.put("success", true);
                response.put("message", "Profile updated successfully!");
            } else {
                response.put("success", false);
                response.put("message", "Failed to update profile");
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error updating profile: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/api/profile")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteDoctorAccount() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication != null ? authentication.getName() : null;
            
            if (username == null) {
                response.put("success", false);
                response.put("message", "Authentication required");
                return ResponseEntity.status(401).body(response);
            }

            Long doctorId = dbService.getDoctorIdByUsername(username);
            if (doctorId == null) {
                response.put("success", false);
                response.put("message", "Doctor profile not found");
                return ResponseEntity.badRequest().body(response);
            }

            boolean deleted = dbService.deleteDoctorAccount(doctorId);
            
            if (deleted) {
                response.put("success", true);
                response.put("message", "Account deleted successfully!");
            } else {
                response.put("success", false);
                response.put("message", "Failed to delete account");
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error deleting account: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
