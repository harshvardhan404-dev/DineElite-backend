package com.dineelite.backend.controller;

import com.dineelite.backend.entity.User;
import com.dineelite.backend.repository.UserRepository;
import com.dineelite.backend.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    @Autowired
    private RecommendationService recommendationService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/dishes")
    public ResponseEntity<?> getDishRecommendations(Authentication authentication) {
        if (authentication == null) return ResponseEntity.status(401).build();
        
        User user = userRepository.findByEmail(authentication.getName())
                .orElse(null);
        
        if (user == null) return ResponseEntity.status(404).build();

        List<Map<String, Object>> recommendations = recommendationService.getDishRecommendations(user.getUserId());
        return ResponseEntity.ok(recommendations);
    }

    @GetMapping("/restaurants")
    public ResponseEntity<?> getRestaurantRecommendations(Authentication authentication) {
        if (authentication == null) return ResponseEntity.status(401).build();
        
        User user = userRepository.findByEmail(authentication.getName())
                .orElse(null);
                
        if (user == null) return ResponseEntity.status(404).build();

        return ResponseEntity.ok(recommendationService.getPersonalizedRecommendations(user.getUserId()));
    }
}
