package com.dineelite.backend.service;

import com.dineelite.backend.dto.RestaurantDTO;
import com.dineelite.backend.entity.Booking;
import com.dineelite.backend.entity.Restaurant;
import com.dineelite.backend.repository.BookingRepository;
import com.dineelite.backend.repository.RestaurantRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    private final BookingRepository bookingRepository;
    private final RestaurantRepository restaurantRepository;
    private final com.dineelite.backend.repository.UserRepository userRepository;
    private final com.dineelite.backend.repository.MenuItemRepository menuItemRepository;

    public RecommendationService(BookingRepository bookingRepository, 
                                 RestaurantRepository restaurantRepository,
                                 com.dineelite.backend.repository.UserRepository userRepository,
                                 com.dineelite.backend.repository.MenuItemRepository menuItemRepository) {
        this.bookingRepository = bookingRepository;
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
        this.menuItemRepository = menuItemRepository;
    }

    public List<Map<String, Object>> getDishRecommendations(Integer userId) {
        com.dineelite.backend.entity.User user = userRepository.findById(userId).orElse(null);
        if (user == null || user.getDietaryPreferences() == null || user.getDietaryPreferences().isEmpty()) {
            return new ArrayList<>();
        }

        Set<String> userPrefs = Arrays.stream(user.getDietaryPreferences().split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        List<com.dineelite.backend.entity.MenuItem> allTaggedItems = menuItemRepository.findByTagsNotNullAndIsAvailableTrue();

        return allTaggedItems.stream()
                .map(item -> {
                    if (item.getTags() == null) return null;
                    
                    Set<String> itemTags = Arrays.stream(item.getTags().split(","))
                            .map(String::trim)
                            .map(String::toLowerCase)
                            .collect(Collectors.toSet());

                    long matchCount = itemTags.stream()
                            .filter(userPrefs::contains)
                            .count();

                    if (matchCount > 0) {
                        double matchPercentage = (double) matchCount / userPrefs.size() * 100;
                        Map<String, Object> result = new HashMap<>();
                        result.put("menuId", item.getMenuId());
                        result.put("itemName", item.getItemName());
                        result.put("description", item.getDescription());
                        result.put("price", item.getPrice());
                        result.put("imageUrl", item.getImageUrl());
                        result.put("restaurantName", item.getRestaurant().getName());
                        result.put("matchPercentage", Math.min(100.0, Math.round(matchPercentage)));
                        return result;
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .sorted((a, b) -> Double.compare((Double) b.get("matchPercentage"), (Double) a.get("matchPercentage")))
                .limit(6)
                .collect(Collectors.toList());
    }

    public List<RestaurantDTO> getPersonalizedRecommendations(Integer userId) {
        List<Booking> userBookings = bookingRepository.findByUser_UserIdOrderByBookingDateDesc(userId);

        if (userBookings.isEmpty()) {
            return getTrendingRecommendations();
        }

        // 1. Get visited restaurant IDs to avoid recommending them again
        Set<Integer> visitedIds = userBookings.stream()
                .map(b -> b.getRestaurant().getRestaurantId())
                .collect(Collectors.toSet());

        // 2. Count cuisine frequency to find favorite categories
        Map<String, Long> cuisineFrequency = userBookings.stream()
                .map(b -> b.getRestaurant().getCuisine())
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(c -> c, Collectors.counting()));

        // 3. Find top cuisine
        String topCuisine = cuisineFrequency.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        if (topCuisine == null) {
            return getTrendingRecommendations();
        }

        // 4. Recommend other restaurants in the top cuisine
        List<Restaurant> recommendations = restaurantRepository.findByCuisine(topCuisine).stream()
                .filter(r -> !visitedIds.contains(r.getRestaurantId()))
                .limit(4)
                .collect(Collectors.toList());

        // 5. If not enough in top cuisine, fill with other cuisines from history
        if (recommendations.size() < 4) {
             List<String> otherCuisines = cuisineFrequency.keySet().stream()
                     .filter(c -> !c.equals(topCuisine))
                     .collect(Collectors.toList());
             
             for (String cuisine : otherCuisines) {
                 if (recommendations.size() >= 4) break;
                 List<Restaurant> more = restaurantRepository.findByCuisine(cuisine).stream()
                         .filter(r -> !visitedIds.contains(r.getRestaurantId()))
                         .filter(r -> recommendations.stream().noneMatch(rec -> rec.getRestaurantId().equals(r.getRestaurantId())))
                         .collect(Collectors.toList());
                 recommendations.addAll(more.subList(0, Math.min(more.size(), 4 - recommendations.size())));
             }
        }

        // 6. Fallback if still empty
        if (recommendations.isEmpty()) {
            return getTrendingRecommendations();
        }

        return mapToDTO(recommendations);
    }

    private List<RestaurantDTO> getTrendingRecommendations() {
        // Simple trending logic: Get oldest or random 4 restaurants for now
        return mapToDTO(restaurantRepository.findAll().stream()
                .limit(4)
                .collect(Collectors.toList()));
    }

    private List<RestaurantDTO> mapToDTO(List<Restaurant> restaurants) {
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
}
