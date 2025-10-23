package com.pg30.webechannellingspringboot.patterns;

import com.pg30.webechannellingspringboot.entities.DoctorEntity;
import com.pg30.webechannellingspringboot.entities.UserEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Factory Pattern for User Creation
 * 
 * This factory creates different types of users based on their role.
 * Instead of creating users manually in controllers, we use this factory
 * to centralize user creation logic.
 */
@Component
public class UserFactory {
    
    private final BCryptPasswordEncoder passwordEncoder;
    
    public UserFactory(BCryptPasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
    
    /**
     * Factory Pattern: Creates a patient user
     */
    public UserEntity createPatient(String firstName, String lastName, String email, 
                                   String phone, String dob, String gender, 
                                   String address, String password) {
        
        UserEntity user = new UserEntity();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPhone(phone);
        user.setDob(dob);
        user.setGender(gender);
        user.setAddress(address);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("PATIENT");
        user.setCreatedAt(LocalDateTime.now());
        
        return user;
    }
    
    /**
     * Factory Pattern: Creates a doctor user with specialization
     */
    public Object[] createDoctor(String firstName, String lastName, String email,
                                String phone, String password,
                                String specialization, BigDecimal fee) {
        
        // Create the base user entity
        UserEntity user = new UserEntity();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPhone(phone);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("DOCTOR");
        user.setCreatedAt(LocalDateTime.now());
        
        // Create the doctor-specific entity
        DoctorEntity doctor = new DoctorEntity();
        doctor.setUser(user);
        doctor.setSpecialization(specialization);
        doctor.setFee(fee);
        
        return new Object[]{user, doctor};
    }
    
    /**
     * Factory Pattern: Creates an admin user
     */
    public UserEntity createAdmin(String firstName, String lastName, String email,
                                 String phone, String password) {
        
        UserEntity user = new UserEntity();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPhone(phone);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("ADMIN");
        user.setCreatedAt(LocalDateTime.now());
        
        return user;
    }
}
