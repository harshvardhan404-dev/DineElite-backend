package com.dineelite.backend.controller;

import com.dineelite.backend.dto.AdvertisementRequest;
import com.dineelite.backend.dto.AdvertisementResponse;
import com.dineelite.backend.entity.Advertisement;
import com.dineelite.backend.entity.Restaurant;
import com.dineelite.backend.repository.RestaurantRepository;
import com.dineelite.backend.service.AdvertisementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/advertisements")
public class AdvertisementController {

    @Autowired
    private AdvertisementService advertisementService;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @GetMapping
    public ResponseEntity<List<AdvertisementResponse>> getAllAds() {
        return ResponseEntity.ok(advertisementService.getAllAdvertisements());
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<AdvertisementResponse>> getRestaurantAds(@PathVariable Integer restaurantId) {
        return ResponseEntity.ok(advertisementService.getAdvertisementsByRestaurant(restaurantId));
    }

    @PostMapping
    public ResponseEntity<?> createAd(@RequestBody AdvertisementRequest request) {
        try {
            System.out.println(">>> RECEIVED AD REQUEST: " + request.getCaption());
            System.out.println(">>> Restaurant ID: " + request.getRestaurantId());
            System.out.println(">>> Media URL: " + request.getMediaUrl());
            System.out.println(">>> Media Type: " + request.getMediaType());
            
            if (request.getRestaurantId() == null) {
                return ResponseEntity.badRequest().body("Error: Restaurant ID is required");
            }

            Restaurant restaurant = restaurantRepository.findById(request.getRestaurantId())
                    .orElseThrow(() -> new RuntimeException("Restaurant not found for ID: " + request.getRestaurantId()));

            Advertisement ad = new Advertisement();
            ad.setRestaurant(restaurant);
            ad.setMediaUrl(request.getMediaUrl());
            ad.setMediaType(request.getMediaType());
            ad.setCaption(request.getCaption());

            AdvertisementResponse response = advertisementService.createAdvertisement(ad);
            System.out.println(">>> AD PUBLISHED SUCCESSFULLY: " + response.getAdId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println(">>> ERROR PUBLISHING AD: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}
