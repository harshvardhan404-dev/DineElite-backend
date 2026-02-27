package com.dineelite.backend.service;

import com.dineelite.backend.entity.MenuItem;
import com.dineelite.backend.entity.Restaurant;
import com.dineelite.backend.repository.MenuItemRepository;
import com.dineelite.backend.repository.RestaurantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MenuService {

    private final MenuItemRepository menuItemRepository;
    private final RestaurantRepository restaurantRepository;

    public MenuService(MenuItemRepository menuItemRepository, RestaurantRepository restaurantRepository) {
        this.menuItemRepository = menuItemRepository;
        this.restaurantRepository = restaurantRepository;
    }

    public List<MenuItem> getMenuByRestaurant(Integer restaurantId) {
        return menuItemRepository.findByRestaurantRestaurantId(restaurantId);
    }

    @Transactional
    public MenuItem addMenuItem(Integer restaurantId, MenuItem item) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));
        item.setRestaurant(restaurant);
        return menuItemRepository.save(item);
    }

    @Transactional
    public MenuItem updateMenuItem(Integer menuId, MenuItem itemDetails) {
        MenuItem item = menuItemRepository.findById(menuId)
                .orElseThrow(() -> new RuntimeException("Menu item not found"));
        
        item.setItemName(itemDetails.getItemName());
        item.setPrice(itemDetails.getPrice());
        item.setIsAvailable(itemDetails.getIsAvailable());
        
        return menuItemRepository.save(item);
    }

    @Transactional
    public void deleteMenuItem(Integer menuId) {
        menuItemRepository.deleteById(menuId);
    }
}
