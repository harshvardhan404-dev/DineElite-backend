package com.dineelite.backend.controller;

import com.dineelite.backend.dto.RegisterRequest;
import com.dineelite.backend.entity.Restaurant;
import com.dineelite.backend.entity.User;
import com.dineelite.backend.repository.RestaurantRepository;
import com.dineelite.backend.repository.UserRepository;
import com.dineelite.backend.service.EmailService;
import com.dineelite.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserService userService;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Error: Email is already in use!");
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // Secure hashing
        user.setRole(request.getRole());
        user.setEnabled(false); // Disabled until verified
        user.setVerificationToken(UUID.randomUUID().toString());
        user.setCreatedAt(LocalDateTime.now());
        user.setDietaryPreferences(request.getDietaryPreferences());

        userRepository.save(user);

        // If ADMIN, create Restaurant Profile
        if ("ADMIN".equals(request.getRole())) {
            Restaurant restaurant = new Restaurant();
            restaurant.setName(request.getRestaurantName());
            restaurant.setAddress(request.getRestaurantAddress());
            restaurant.setOpeningTime(LocalTime.parse(request.getOpeningTime()));
            restaurant.setClosingTime(LocalTime.parse(request.getClosingTime()));
            restaurant.setImageUrl(request.getRestaurantImageUrl());
            restaurant.setDescription(request.getRestaurantDescription());
            restaurant.setAdmin(user);
            restaurant.setCreatedAt(LocalDateTime.now());
            restaurant.setHouseRules("Default house rules. Please update in dashboard.");
            restaurant.setDepositAmount(0.0);
            
            restaurantRepository.save(restaurant);
            System.out.println(">>> Restaurant profile created for admin: " + user.getEmail());
        }

        // Send Email
        emailService.sendVerificationEmail(user.getEmail(), user.getVerificationToken());

        return ResponseEntity.ok("User registered successfully. Please check your email for verification.");
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestParam("token") String token) {
        User user = userRepository.findByVerificationToken(token);
        if (user == null) {
            return ResponseEntity.badRequest().body("Error: Invalid verification token!");
        }

        user.setEnabled(true);
        user.setVerificationToken(null);
        userRepository.save(user);

        return ResponseEntity.ok("User verified successfully. You can now login.");
    }

    @GetMapping("/user/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null) return ResponseEntity.status(401).build();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .map(user -> ResponseEntity.ok(user))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/user")
    public ResponseEntity<?> deleteAccount(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body("Error: Unauthorized");
        }

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        userService.deleteUser(user.getUserId());
        System.out.println(">>> Account deleted for user: " + email);

        return ResponseEntity.ok("Account deleted successfully.");
    }

    @PutMapping("/user/dietary")
    public ResponseEntity<?> updateDietaryPreferences(@RequestBody String dietaryPreferences, Authentication authentication) {
        if (authentication == null) return ResponseEntity.status(401).build();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setDietaryPreferences(dietaryPreferences);
        userRepository.save(user);
        return ResponseEntity.ok(user);
    }
}
