package com.dineelite.backend.controller;

import com.dineelite.backend.entity.MenuItem;
import com.dineelite.backend.service.MenuService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menu")
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping("/restaurant/{restaurantId}")
    public List<MenuItem> getMenu(@PathVariable Integer restaurantId) {
        return menuService.getMenuByRestaurant(restaurantId);
    }

    @PostMapping("/restaurant/{restaurantId}")
    public MenuItem addMenuItem(@PathVariable Integer restaurantId, @RequestBody MenuItem item) {
        return menuService.addMenuItem(restaurantId, item);
    }

    @PutMapping("/{menuId}")
    public MenuItem updateMenuItem(@PathVariable Integer menuId, @RequestBody MenuItem item) {
        return menuService.updateMenuItem(menuId, item);
    }

    @DeleteMapping("/{menuId}")
    public void deleteMenuItem(@PathVariable Integer menuId) {
        menuService.deleteMenuItem(menuId);
    }
}
