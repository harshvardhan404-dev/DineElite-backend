package com.dineelite.backend.service;

import com.dineelite.backend.dto.RestaurantDTO;
import com.dineelite.backend.entity.Restaurant;
import com.dineelite.backend.repository.RestaurantRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    public RestaurantService(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    public List<RestaurantDTO> getAllRestaurants() {

        List<Restaurant> restaurants = restaurantRepository.findAll();

        return restaurants.stream()
                .map(r -> new RestaurantDTO(
                        r.getRestaurantId(),
                        r.getName(),
                        r.getAddress(),
                        r.getOpeningTime(),
                        r.getClosingTime(),
                        r.getImageUrl(),
                        r.getDepositAmount(),
                        r.getDescription(),
                        r.getHouseRules(),
                        r.getCuisine(),
                        r.getLatitude(),
                        r.getLongitude()
                ))
                .collect(Collectors.toList());
    }

    public RestaurantDTO findByAdmin(Integer userId) {
        Restaurant r = restaurantRepository.findByAdminUserId(userId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found for this admin"));

        return new RestaurantDTO(
                r.getRestaurantId(),
                r.getName(),
                r.getAddress(),
                r.getOpeningTime(),
                r.getClosingTime(),
                r.getImageUrl(),
                r.getDepositAmount(),
                r.getDescription(),
                r.getHouseRules(),
                r.getCuisine(),
                r.getLatitude(),
                r.getLongitude()
        );
    }

    public RestaurantDTO updateRestaurant(Integer id, RestaurantDTO restaurantDTO) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        restaurant.setName(restaurantDTO.getName());
        restaurant.setAddress(restaurantDTO.getAddress());
        restaurant.setOpeningTime(restaurantDTO.getOpeningTime());
        restaurant.setClosingTime(restaurantDTO.getClosingTime());
        restaurant.setImageUrl(restaurantDTO.getImageUrl());
        restaurant.setDepositAmount(restaurantDTO.getDepositAmount());
        restaurant.setDescription(restaurantDTO.getDescription());
        restaurant.setHouseRules(restaurantDTO.getHouseRules());
        restaurant.setCuisine(restaurantDTO.getCuisine());
        restaurant.setLatitude(restaurantDTO.getLatitude());
        restaurant.setLongitude(restaurantDTO.getLongitude());

        Restaurant updated = restaurantRepository.save(restaurant);

        return new RestaurantDTO(
                updated.getRestaurantId(),
                updated.getName(),
                updated.getAddress(),
                updated.getOpeningTime(),
                updated.getClosingTime(),
                updated.getImageUrl(),
                updated.getDepositAmount(),
                updated.getDescription(),
                updated.getHouseRules(),
                updated.getCuisine(),
                updated.getLatitude(),
                updated.getLongitude()
        );
    }
}
