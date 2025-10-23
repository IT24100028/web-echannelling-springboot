package com.pg30.webechannellingspringboot.controllers;

import com.pg30.webechannellingspringboot.DTOs.UserDTO;
import com.pg30.webechannellingspringboot.database.repositories.UserRepository;
import com.pg30.webechannellingspringboot.entities.UserEntity;
import com.pg30.webechannellingspringboot.patterns.UserFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping(value = "/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    @Autowired
    private UserFactory userFactory;

    @RequestMapping(value = "/signin",method = RequestMethod.GET)
    public String userLogin() {
        return "signin";
    }

    @GetMapping("/signup")
    public String showSignupForm(Model model) {
        model.addAttribute("user", new UserEntity());
        return "signup";
    }

    @PostMapping("/signup")
    public String createUser(@RequestParam String firstName,
                             @RequestParam String lastName,
                             @RequestParam String email,
                             @RequestParam String phone,
                             @RequestParam String dob,
                             @RequestParam String gender,
                             @RequestParam String address,
                             @RequestParam String password,
                             @RequestParam String confirmPassword,
                             RedirectAttributes redirectAttributes) {

        try {
            if (!password.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("error", "Passwords do not match!");
                return "redirect:/user/signup";
            }

            if (userRepository.findByEmail(email).isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Email already registered!");
                return "redirect:/user/signup";
            }

            UserEntity newUser = userFactory.createPatient(
                firstName, lastName, email, phone, dob, gender, address, password
            );

            userRepository.save(newUser);
            redirectAttributes.addFlashAttribute("success", "Account created successfully! You can now sign in.");
            return "redirect:/user/signin";

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error creating account: " + e.getMessage());
            return "redirect:/user/signup";
        }
    }

    @GetMapping("/indexMY")
    public String showIndexMY(Model model) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/user/signin";
        }

        String email;
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else {
            email = principal.toString();
        }

        model.addAttribute("userEmail", email);
        return "indexMY";
    }

    @GetMapping("/userProfile")
    public String showUserProfile(Model model) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/user/signin";
        }

        String email;
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else {
            email = principal.toString();
        }

        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        model.addAttribute("user", convertToDTO(userEntity));
        return "userProfile";
    }

    @PostMapping("/update")
    public String updateUser(@ModelAttribute UserDTO userDTO,
                             RedirectAttributes redirectAttributes) {
        try {
            UserEntity existingUser = userRepository.findByEmail(userDTO.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            existingUser.setFirstName(userDTO.getFirstName());
            existingUser.setLastName(userDTO.getLastName());
            existingUser.setPhone(userDTO.getPhone());
            existingUser.setDob(userDTO.getDob());
            existingUser.setGender(userDTO.getGender());
            existingUser.setAddress(userDTO.getAddress());

            userRepository.save(existingUser);
            redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error updating profile: " + e.getMessage());
        }

        return "redirect:/user/userProfile?email=" + URLEncoder.encode(userDTO.getEmail(), StandardCharsets.UTF_8);
    }

    @PostMapping("/delete")
    public String deleteUser(@RequestParam String email,
                             RedirectAttributes redirectAttributes) {
        try {
            userRepository.deleteByEmail(email);
            redirectAttributes.addFlashAttribute("success", "Account deleted successfully!");
            return "redirect:/";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error deleting account: " + e.getMessage());
            return "redirect:/user/userProfile?email=" + URLEncoder.encode(email, StandardCharsets.UTF_8);
        }
    }

    private UserDTO convertToDTO(UserEntity userEntity) {
        return new UserDTO(
                userEntity.getUserId(),
                userEntity.getFirstName(),
                userEntity.getLastName(),
                userEntity.getEmail(),
                userEntity.getPhone(),
                userEntity.getDob(),
                userEntity.getGender(),
                userEntity.getAddress(),
                userEntity.getRole(),
                userEntity.getCreatedAt()
        );
    }
}
