package com.dineelite.backend.controller;

import com.dineelite.backend.dto.RestaurantDTO;
import com.dineelite.backend.service.RecommendationService;
import com.dineelite.backend.service.RestaurantService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;
    private final com.dineelite.backend.service.BookingService bookingService;
    private final RecommendationService recommendationService;

    public RestaurantController(RestaurantService restaurantService, 
                                com.dineelite.backend.service.BookingService bookingService,
                                RecommendationService recommendationService) {
        this.restaurantService = restaurantService;
        this.bookingService = bookingService;
        this.recommendationService = recommendationService;
    }

    @GetMapping("/recommendations")
    public List<RestaurantDTO> getPersonalizedRecommendations(@RequestParam Integer userId) {
        return recommendationService.getPersonalizedRecommendations(userId);
    }

    @GetMapping
    public List<RestaurantDTO> getAllRestaurants() {
        return restaurantService.getAllRestaurants();
    }

    @GetMapping("/{id}/menu")
    public List<com.dineelite.backend.dto.MenuItemResponse> getPopularMenu(@PathVariable Integer id) {
        return bookingService.getPopularMenu(id);
    }

    @PutMapping("/{id}")
    public RestaurantDTO updateRestaurant(@PathVariable Integer id, @RequestBody RestaurantDTO restaurantDTO) {
        return restaurantService.updateRestaurant(id, restaurantDTO);
    }

    @GetMapping("/admin/{userId}")
    public RestaurantDTO getRestaurantByAdmin(@PathVariable Integer userId) {
        return restaurantService.findByAdmin(userId);
    }
}
